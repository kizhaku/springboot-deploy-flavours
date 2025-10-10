Deploying app locally in Minikube
- Start minikube
    <code>minikube start --memory=4096 --cpus=4</code>
- Check the context
    <code>kubectl config current-context</code>
- Deploy app
  - <code> kubectl apply -f infra/kubernetes/local/deployment.yml</code>
- Create service
  - <code> kubectl apply -f infra/kubernetes/local/service.yml</code>
- Verify
  - Check the service
    - <code>kubectl get svc</code>
      NAME                TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
      kubernetes          ClusterIP      10.96.0.1       <none>        443/TCP          2y186d
      springapp-service   LoadBalancer   10.106.70.249   <pending>    8081:32251/TCP   4d23h
  
    If minikube is runnning with docker driver, minikube ip may not route to host. Use a service to access it from hsot machine.
    Within the cluster the service will be accessible at http://<service-name>:<port>/actuator/health
  
   Can port forward on the host with kubectl port-forward service/springapp-service 8081:8081
   Check application health at: http://127.0.0.1:8081/actuator/health
   Api at: http://127.0.0.1:8081/swagger-ui/index.html

Install Tekton pipeline
Note: 
    Might run into issue installing Tekton if the Kubernetes version doesn't match.
    At this point of writing the version was set at 1.29.0
    <code>minikube start --kubernetes-version=v1.29.0 --cpus=4 --memory=4096</code>

Create a namespace for the pipeline
  - kubectl create namespace tekton-pipelines
  - Patch namespaces
      <code>kubectl label namespace tekton-pipelines app.kubernetes.io/managed-by=Helm --overwrite</code>
      <code>kubectl annotate namespace tekton-pipelines meta.helm.sh/release-name=tekton --overwrite</code>
      <code>kubectl annotate namespace tekton-pipelines meta.helm.sh/release-namespace=tekton-pipelines --overwrite</code>
Install Tekton
  <code>helm install tekton cdf/tekton-pipeline --version 1.4.0 -n tekton-pipelines</code>
Install Tekton dashboard
  - <code>kubectl apply -f https://storage.googleapis.com/tekton-releases/dashboard/latest/release-full.yaml</code>
  - Verify services
    <code>kubectl get svc -n tekton-pipelines</code>
  - Start dashboard
    - <code>kubectl -n tekton-pipelines port-forward svc/tekton-dashboard 9097:9097</code>
    - Set privileges for pipeline to run:
      <code>
      kubectl label --overwrite ns tekton-pipelines \
      pod-security.kubernetes.io/enforce=privileged \
      pod-security.kubernetes.io/audit=privileged \
      pod-security.kubernetes.io/warn=privileged
      </code>
- Test pipeline with a sample pipeline
  - Pipeline
    - <code>kubectl apply -f infra/kubernetes/tekton/pipeline/test/pipeline-test.yml</code>
  - Pipeline run
    - <code>kubectl create -f infra/kubernetes/tekton/pipeline/test/pipeline-test-run.yml</code>

Build and deploy app:
- Create secret for docker push to repo
  <code>
    kubectl create secret docker-registry dockerhub-secret \
  --docker-username=<your-dockerhub-username> \
  --docker-password=<your-dockerhub-pat> \
  --docker-email=<your-email> \
  -n tekton-pipelines
    </code>
Install Git clone task
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/git-clone/0.9/git-clone.yaml -n tekton-pipelines</code>
Install Gradle task
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/gradle/0.3/gradle.yaml -n tekton-pipelines</code>
Install buildah task for building/pushing
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/buildah/0.9/buildah.yaml -n tekton-pipelines</code>
Install SonarQube
  - Create namespace for sonarqube
  <code>kubectl create namespace sonarqube</code>
  - Add a values.yml for Helm infra/kubernetes/sonarqube/values.yml
    <code>helm repo add sonarqube https://SonarSource.github.io/helm-chart-sonarqube</code>
  - Create a monitoring secret
    <code>kubectl create secret generic sonarqube-monitoring-passcode -n sonarqube --from-literal=passcode=sonarPass123</code>
  - Install sonarqube
    <code>helm install sonarqube sonarqube/sonarqube -n sonarqube -f infra/kubernetes/sonarqube/values.yml</code>
  - Check sonarqube service
    <code>kubectl get svc -n sonarqube</code>
  - Access UI by port forwarding
    <code>kubectl port-forward service/sonarqube-sonarqube 9000:9000 -n sonarqube</code>
  - Create a secret for Sonar. Access UI, under security create a new token
    <code>kubectl create secret generic sonar-auth -n tekton-pipelines --from-literal=SONAR_TOKEN=<token></code>

Create and run the pipeline
  - Create PVC for workspace
        <code>kubectl apply -f infra/kubernetes/tekton/pipeline/tekton-workspace-pvc.yml</code>
  - Create a service account
    <code>kubectl apply -f infra/kubernetes/tekton/pipeline/sa-build-bot.yml</code>
  - Create the pipeline
    <code>kubectl apply -f infra/kubernetes/tekton/pipeline/pipeline-springapp-build-and-push.yml</code>
  - Run the pipeline
    <code>kubectl create -f infra/kubernetes/tekton/pipeline/pipelinerun-springapp.yml</code>

