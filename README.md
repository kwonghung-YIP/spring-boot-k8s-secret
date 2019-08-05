# Introduction
This repo shows how to bind k8s secrets into spring boot password properties (e.g. spring.database.password).

## application.yml
The sample [application.yml](/src/main/resources/application.yml) has following properties bind to other environment properties, which utimately are read from k8s secrets:

* the user credential for mysql db: spring.datasource.username, spring.datasource.password
* the password for redis db: spring.redis.password
* the embedded user credential for spring security: spring.security.user.name, spring.security.user.password

The list property **k8s.secret-mount** at the bottom includes all paths where the k8s secrets being mounted into our spring boot container, taking **mysql-secret** as example, the secret mounts under the path **/usr/local/k8s/mysql-sevret** within the container, and it contains 2 data entries: **mysql-user** and **mysql-password** which are defined in [manifest]().  the taking the property **${k8s-secret.mysql-secret.mysql-passwd}** as example, the first portion **k8s-secret** is the prefix to indicate that property is read from k8s secret, and the second portion **mysql-secret** is mapped with **/usr/local/k8s/mysql-secret**, finally the third portion, **mysql-passwd** is the entry provided within the secret.

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

## k8s deployment manifest for spring boot application
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
## EnvironmentPostProcessor Implementation

```properties
org.springframework.boot.env.EnvironmentPostProcessor=hung.org.K8sSecretPostProcessor
```

# Run this demo in [Katacoda - Kubernetes Playground](https://www.katacoda.com/courses/kubernetes/playground)
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

# Run this demo in your local Microk8s
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
