# Introduction
This repo shows how to bind k8s secrets into spring boot password properties (e.g. spring.database.password).

# application.yaml
The sample [application.yml](/src/main/resources/application.yml) in this demo has the password properties for mysql and redis db, also the predefined user login for spring-security.

```yaml
spring:
  profiles: k8s
 
  datasource:
    url: jdbc:mysql://mysql-svc:3306/testdb
    username: ${k8s-secret.mysql-secret.mysql-user}
    password: ${k8s-secret.mysql-secret.mysql-passwd}
  
  redis:
    host: redis-svc
    port: 6379
    password: ${k8s-secret.redis-secret.redis-passwd}

  security:
    user:
      name: ${k8s-secret.user-secret.login}
      password: ${k8s-secret.user-secret.passwd}
      roles:
      - admin

k8s:
  secret-mount:
  - /usr/local/k8s/mysql-secret
  - /usr/local/k8s/redis-secret
  - /usr/local/k8s/user-secret
```

# k8s deployment manifest for spring boot application
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: springboot-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      pod-name: springboot-pod
  template:
    metadata:
      name: springboot-pod
      labels:
        pod-name: springboot-pod
    spec:
      containers:
      - name: springboot-app
        image: kwonghung/spring-boot-k8s-secret:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: k8s,formAuth
        volumeMounts:
        - name: mysql-secret
          mountPath: "/usr/local/k8s/mysql-secret"
          readOnly: true
        - name: redis-secret
          mountPath: "/usr/local/k8s/redis-secret"
          readOnly: true
        - name: user-secret
          mountPath: "/usr/local/k8s/user-secret"
          readOnly: true         
      volumes:
      - name: mysql-secret
        secret:
          secretName: mysql-secret
      - name: redis-secret
        secret:
          secretName: redis-secret
      - name: user-secret
        secret:
          secretName: user-secret
```

## Run the demo in [Katacoda - Kubernetes Playground](https://www.katacoda.com/courses/kubernetes/playground)
```bash
git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest

./run-katacoda.sh
### --- or ---
kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-app.yaml

curl -v --user john:abcd1234 <springboot-svc's ClusterIP>:8080
```

## Run the demo with you local Microk8s
```bash
git clone https://github.com/kwonghung-YIP/spring-boot-k8s-secret.git
cd spring-boot-k8s-secret/k8s-manifest

./run-microk8s.sh
### --- or ---
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
