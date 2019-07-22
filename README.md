# spring-boot-k8s-secret
Binding spring boot password (e.g. spring.database.password) property into k8s secret

## Run it with microk8s
```bash
git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest
kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-app.yaml
kubectl apply -f ingress-controller.yaml
```
