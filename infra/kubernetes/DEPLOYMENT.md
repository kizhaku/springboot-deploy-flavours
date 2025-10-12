# Deploying App Locally in Minikube

- **Start Minikube**

  ```bash
  minikube start --memory=4096 --cpus=4
  ```

- **Create app deployment manifest**

  ```bash
   kubectl apply -f infra/kubernetes/local/deployment.yml
  ```

- **Create app service manifest**

  ```bash
   kubectl apply -f infra/kubernetes/local/service.yml
  ```

## Install Tekton Pipeline

> Note: 
> Might run into issue installing Tekton if the Kubernetes version doesn't match.
> At this point of writing the version was set at 1.29.0

```bash
minikube start --kubernetes-version=v1.29.0 --cpus=4 --memory=4096
```

- **Create a namespace for the pipeline**

  ```bash
  kubectl create namespace tekton-pipelines
  ```

- **Patch namespaces**

  ```bash
  kubectl label namespace tekton-pipelines app.kubernetes.io/managed-by=Helm --overwrite
  kubectl annotate namespace tekton-pipelines meta.helm.sh/release-name=tekton --overwrite
  kubectl annotate namespace tekton-pipelines meta.helm.sh/release-namespace=tekton-pipelines --overwrite
  ```

- **Install Tekton**

  ```bash
  helm install tekton cdf/tekton-pipeline --version 1.4.0 -n tekton-pipelines
  ```

- **Install Tekton dashboard**

  ```bash
  kubectl apply -f https://storage.googleapis.com/tekton-releases/dashboard/latest/release-full.yaml
  ```

- **Verify services**

  ```bash
  kubectl get svc -n tekton-pipelines
  ```

- **Start dashboard**

  ```bash
  kubectl -n tekton-pipelines port-forward svc/tekton-dashboard 9097:9097
  ```

- **Set privileges for pipeline to run**

  ```bash
  kubectl label --overwrite ns tekton-pipelines \
    pod-security.kubernetes.io/enforce=privileged \
    pod-security.kubernetes.io/audit=privileged \
    pod-security.kubernetes.io/warn=privileged
  ```

- **Optional: Test pipeline with a sample pipeline**

  - Pipeline

    ```bash
    kubectl apply -f infra/kubernetes/tekton/pipeline/test/pipeline-test.yml
    ```

  - Pipeline run

    ```bash
    kubectl create -f infra/kubernetes/tekton/pipeline/test/pipeline-test-run.yml
    ```

## Build and Deploy App

- **Create secret for docker push to repo**

  ```bash
  kubectl create secret docker-registry dockerhub-secret \
    --docker-username=<dockerhub-username> \
    --docker-password=<access-token> \
    --docker-email=<email> \
    -n tekton-pipelines
  ```

- **Create secret for image pull during deployment**

  ```bash
  kubectl create secret docker-registry dockerhub-secret \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=<dockerhub-username> \
    --docker-password=<access-token> \
    -n springapp
  ```

- **Create token for write access to repo**

  This will be used to update image tag.

  ```bash
  kubectl create secret generic git-credentials \
    --from-literal=username=tekton-bot \
    --from-literal=password=<access-token> \
    -n tekton-pipelines
  ```

- **Install Git clone task**

  ```bash
  kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/git-clone/0.9/git-clone.yaml -n tekton-pipelines
  ```

- **Install Gradle task**

  ```bash
  kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/gradle/0.3/gradle.yaml -n tekton-pipelines
  ```

- **Install buildah task for building/pushing**

  ```bash
  kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/buildah/0.9/buildah.yaml -n tekton-pipelines
  ```

## Install SonarQube

- **Create namespace for sonarqube**

  ```bash
  kubectl create namespace sonarqube
  ```

- **Add a values.yml for Helm infra/kubernetes/sonarqube/values.yml**

  ```bash
  helm repo add sonarqube https://SonarSource.github.io/helm-chart-sonarqube
  ```

- **Create a monitoring secret**

  ```bash
  kubectl create secret generic sonarqube-monitoring-passcode -n sonarqube --from-literal=passcode=<passcode>
  ```

- **Install sonarqube**

  ```bash
  helm install sonarqube sonarqube/sonarqube -n sonarqube -f infra/kubernetes/sonarqube/values.yml
  ```

- **Check sonarqube service**

  ```bash
  kubectl get svc -n sonarqube
  ```

- **Access UI by port forwarding**

  ```bash
  kubectl port-forward service/sonarqube-sonarqube 9000:9000 -n sonarqube
  ```

- **Create a secret for Sonar**

  Access UI, under security create a new token.

  ```bash
  kubectl create secret generic sonar-auth -n tekton-pipelines --from-literal=SONAR_TOKEN=<token>
  ```

- **Add Secrets to the Build Bot**

```bash
kubectl apply -f infra/kubernetes/tekton/service/sa-build-bot.yml
```

## Install ArgoCD

- **Add repo**

  ```bash
  helm repo add argo https://argoproj.github.io/argo-helm
  ```

- **Create namespace**

  ```bash
  kubectl create namespace argocd
  ```

- **Install**

  ```bash
  helm install argocd argo/argo-cd -n argocd
  ```

- **Access Dashboard**

  ```bash
  kubectl port-forward service/argocd-server -n argocd 8080:443
  ```

  http://localhost:8080

- **Create manifest for repo**

  ```
  infra/kubernetes/argocd/springapp.yml
  ```

- **Register repo**

  This should register the repo and ArgoCD will sync the cluster with the expected. The path provided is path: infra/kubernetes/local.
  ArgoCD monitors the Kubernetes manifests and applies it to maintain the state. So when Tekton pushes a new image and updates the image tag, ArgoCD will apply the manifest, which will initiate the app deployment.

  ```bash
  kubectl apply -f infra/kubernetes/argocd/springapp.yml -n argocd
  ```

  - **Verify app deployment**

  ```bash
  kubectl get svc -n springapp
  NAME                TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
  springapp-service   LoadBalancer   10.107.103.214   <pending>     8081:32251/TCP   178m
  ```
  ```bash
  kubectl port-forward svc/springapp-service 32251:8081 -n springapp
  ```

  Check application health at: http://127.0.0.1:32251/actuator/health
  Api documentation at: http://127.0.0.1:32251/swagger-ui/index.html

  **Optional through CLI:**

  - Install argocd CLI.
  - Log in:

    ```bash
    argocd login localhost:8080 --username admin --password <password> --insecure
    ```

  - Add repo:

    ```bash
    argocd repo add https://github.com/kizhaku/springboot-deploy-flavours.git --username <github-username> --password <token>
    ```

  - Create app:

    ```bash
    argocd app create springapp \
      --repo https://github.com/kizhaku/springboot-deploy-flavours.git \
      --path infra/kubernetes/local \
      --dest-server https://kubernetes.default.svc \
      --dest-namespace springapp \
      --sync-policy automated \
      --auto-prune \
      --self-heal
    ```

## Create and Run the Pipeline

- **Create PVC for workspace**

  ```bash
  kubectl apply -f infra/kubernetes/tekton/pipeline/tekton-workspace-pvc.yml
  ```

- **Create a service account with access to Docker, Sonar and Github secret**

  ```bash
  kubectl apply -f infra/kubernetes/tekton/pipeline/sa-build-bot.yml
  ```

- **Create the pipeline**

  ```bash
  kubectl apply -f infra/kubernetes/tekton/pipeline/pipeline-springapp.yml
  ```

- **Run the pipeline**

  ```bash
  kubectl create -f infra/kubernetes/tekton/pipeline/pipelinerun-springapp.yml
  ```

Tekton will build and publish the image to Docker hub and update the deployment.yml manifest with new image. ArgoCD monitoring infra/kubernetes/local will detect this commit and sync the cluster with new deployment.