#!/usr/bin/env bash

files=$(dirname ${BASH_SOURCE[0]})

kubectl delete -f $files/liquibase.yml
kubectl delete -f $files/target
