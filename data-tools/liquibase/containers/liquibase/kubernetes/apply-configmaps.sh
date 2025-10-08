#!/usr/bin/env bash

script_files=$(dirname ${BASH_SOURCE[0]})/scripts
lb_files=$(dirname ${BASH_SOURCE[0]})/../target/liquibase-changelog/liquibase
output=$(dirname ${BASH_SOURCE[0]})/target

mkdir -p $output
kubectl create configmap liquibase-changelog-configmap --from-file $lb_files/changelog --dry-run=client -o yaml > $output/liquibase-changelog-configmap.yml
kubectl create configmap liquibase-scripts-configmap --from-file $script_files --dry-run=client -o yaml > $output/liquibase-scripts-configmap.yml

kubectl apply -f $output
