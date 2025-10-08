#!/usr/bin/env bash

files=$(dirname ${BASH_SOURCE[0]})

$files/apply-configmaps.sh
kubectl apply -f $files/liquibase.yml
