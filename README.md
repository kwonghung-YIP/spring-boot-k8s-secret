# How to mount kubernetes secret into "spring.datasource.password"
The [application.yml](/src/main/resources/application.yml) in this repo has following properties which are mounted to kubernetes secrets:

* **spring.datasource.username** and **spring.datasource.password** - for mysql database user credential
* **spring.redis.password** - for redis db password
* **spring.security.user.name** and **spring.security.user.password** - for spring security user credential

The list property **k8s.secret-mount** at the bottom includes all paths where kubernetes secrets being mounted into our spring boot container. Take the first path **/usr/local/k8s/mysql-secret** as example, where it is the kubernetes secret **mysql-secret** being mounted as files into the container. The **mysql-secret** has a data entry **mysql-password**, which is pre-loaded and available as an environment property **${k8s-secret.mysql-secret.mysql-passwd}** within spring boot configuration. That property is refered by the default data source password property **spring.datasource.password**, and that's how to mount a k8s secret entry into the spring boot password property. 

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

The **${k8s-secret.mysql-secret.mysql-passwd}** has 3 portions, the first portion **k8s-secret** is the prefix to indicate that property bind to k8s secret, the second portion **mysql-secret** maps with the last folder with the path **/usr/local/k8s/mysql-secret**, finally the third portion, **mysql-passwd** is the data entry defined in the secret.

# Deploy the spring boot application into k8s
The spring boot application in this repo is available in [docker hub](https://cloud.docker.com/u/kwonghung/repository/docker/kwonghung/spring-boot-k8s-secret) and following [manifest](/k8s-manifest/springboot-app.yaml) defines how to deploy it into k8s platform. Pay attention to the **volumneMounts** option, the **mountPath** of 3 k8s secrets are matched with those paths defined in above applications.yml.

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

# Customize EnvironmentPostProcessor to load kubernetes secrets into spring boot configuration
The [Spring Boot Reference Document](https://docs.spring.io/spring-boot/docs/2.2.0.M4/reference/html/#howto-customize-the-environment-or-application-context) mentions about how to customize the Environment by using EnvironmentPostProcessor. The [K8sSecretPostProcessor](/src/main/java/hung/org/K8sSecretPostProcessor.java) in this repo will load the mounted secret files as environment properties. The class also need to be registered in the [META-INF/spring.factories](/src/main/resources/META-INF).

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
