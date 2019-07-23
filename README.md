# spring-boot-k8s-secret
Binding spring boot password (e.g. spring.database.password) property into k8s secret

## Run the demo in [Katacoda - Kubernetes Playground](https://www.katacoda.com/courses/kubernetes/playground)
```bash
git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest

kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-app.yaml
```

## Run the demo with you local Microk8s
```bash
git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest

sudo microk8s.reset
sudo microk8s.enable dns ingress
sudo iptables -P FORWARD ACCEPT

kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-app.yaml
kubectl apply -f ingress-controller.yaml
```
