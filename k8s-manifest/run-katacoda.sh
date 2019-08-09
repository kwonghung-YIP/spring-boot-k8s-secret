#!/bin/bash

kubectl apply -f secrets.yaml
kubectl create configmap redis-config --from-file redis.conf
kubectl apply -f redis-service.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f springboot-basic-auth.yaml

sleep 10
kubectl get all

CLUSERIP=`kubectl get service springboot-svc -o=jsonpath='{.spec.clusterIP}'`
curl -v -user john:abcd1234 $CLUSTERIP:8080
