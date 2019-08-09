#!/bin/bash

kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-basic-auth.yaml

echo "Sleep for 1 min to wait for service start up..."
sleep 60
kubectl get all

#CLUSTER_IP=`kubectl get service springboot-svc -o=jsonpath='{.spec.clusterIP}'`
#curl -v --user john:abcd1234 $CLUSTER_IP:8080
