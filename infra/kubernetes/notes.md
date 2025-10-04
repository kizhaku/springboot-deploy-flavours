Deploying app locally in Minikube
- Start minikube
    <code>minikube start --memory=4096 --cpus=4</code>
- Check the context
    <code>kubectl config current-context</code>
- Deploy app
  - <code> kubectl apply -f deploy/kubernetes/local/deployment.yml</code>
- Create service
  - <code> kubectl apply -f deploy/kubernetes/local/service.yml</code>
- Verify
  - Create tunnel to access via an external IP
    <code>minikube tunnel</code>
  - Check the service
    - <code>kubectl get svc</code>
      NAME                TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
      kubernetes          ClusterIP      10.96.0.1       <none>        443/TCP          2y186d
      springapp-service   LoadBalancer   10.106.70.249   127.0.0.1     8081:32251/TCP   4d23h
  - Check application health at: http://127.0.0.1:8081/actuator/health
  - Api at: http://127.0.0.1:8081/swagger-ui/index.html

Install Tekton pipeline
Note: 
    Might run into issue installing Tekton if the Kubernetes version doesnt match.
    At this point of writing the version was set at 1.20.0

    <code>minikube start --kubernetes-version=v1.29.0 --cpus=4 --memory=4096</code>

- Create a namespace for the pipeline
  - kubectl create namespace tekton-pipelines
  - Patch namespaces
      <code>kubectl label namespace tekton-pipelines app.kubernetes.io/managed-by=Helm --overwrite</code>
      <code>kubectl annotate namespace tekton-pipelines meta.helm.sh/release-name=tekton --overwrite</code>
      <code>kubectl annotate namespace tekton-pipelines meta.helm.sh/release-namespace=tekton-pipelines --overwrite</code>
- Install Tekton
  - <code>helm install tekton cdf/tekton-pipeline \                                       
    --version 1.4.0 \
    -n tekton-pipelines</code>
- Install Tekton dashboard
  - <code>kubectl apply -f \                                             
    https://storage.googleapis.com/tekton-releases/dashboard/latest/release-full.yaml</code>
  - Start dashboard
    - <code>kubectl -n tekton-pipelines port-forward svc/tekton-dashboard 9097:9097</code>
    - Set previleges for pipeline to run:
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

Build app:
- Create secret for docker push to repo
  <code>
    kubectl create secret docker-registry dockerhub-secret \
  --docker-username=<your-dockerhub-username> \
  --docker-password=<your-dockerhub-pat> \
  --docker-email=<your-email> \
  -n tekton-pipelines
    </code>
- Create a service account
  <code>kubectl apply -f infra/kubernetes/tekton/pipeline/sa-build-bot.yml</code>
- Install Git clone task
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/git-clone/0.9/git-clone.yaml -n tekton-pipelines</code>
- Install Gradle task
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/gradle/0.3/gradle.yaml -n tekton-pipelines</code>
- Install buildah task for building/pushing
  <code>kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/buildah/0.9/buildah.yaml -n tekton-pipelines</code>
- Create PVC for workspace
  <code>kubectl apply -f infra/kubernetes/tekton/pipeline/tekton-workspace-pvc.yml</code>
- Create the pipeline
  <code>kubectl apply -f infra/kubernetes/tekton/pipeline/pipeline-springapp-build-and-push.yml</code>
- Run the pipeline
  <code>kubectl create -f infra/kubernetes/tekton/pipeline/pipelinerun-springapp.yml</code>