sudo microk8s.reset
sudo microk8s.enable dns ingress
sudo iptables -P FORWARD ACCEPT

git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest
kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-app.yaml
kubectl apply -f ingress-controller.yaml
