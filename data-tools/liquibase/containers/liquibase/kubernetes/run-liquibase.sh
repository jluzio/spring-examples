#!/usr/bin/env bash
kubectl exec deploy/liquibase-tools liquibase -it -- bash
