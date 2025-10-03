Deploying app locally in Minikube
- Start minikube
    - <code>minikube start --memory=4096 --cpus=4</code>
- Check the context
  - <code>kubectl config current-context</code>
- Deploy app
  - <code> kubectl apply -f deploy/kubernetes/local/deployment.yml
- Create service
  - <code> kubectl apply -f deploy/kubernetes/local/service.yml
- Verify
  - Create tunnel to access via an external IP
    - minikube tunnel
  - Check the service
    - kubectl get svc
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
  - helm install tekton cdf/tekton-pipeline \                                       
    --version 1.4.0 \
    -n tekton-pipelines
- Install Tekton dashboard
  - kubectl apply -f \                                             
    https://storage.googleapis.com/tekton-releases/dashboard/latest/release-full.yaml
  - Start dashboard
    -  kubectl -n tekton-pipelines port-forward svc/tekton-dashboard 9097:9097
    - Set previleges for pipeline to run:
      kubectl label --overwrite ns tekton-pipelines \
      pod-security.kubernetes.io/enforce=privileged \
      pod-security.kubernetes.io/audit=privileged \
      pod-security.kubernetes.io/warn=privileged
- Test pipeline with a sample pipeline
  - Pipeline
    - kubectl apply -f deploy/kubernetes/local/pipeline-test.yml
  - Pipeline run
    - kubectl create -f deploy/kubernetes/local/pipeline-test-run.yml