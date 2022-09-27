#!/usr/bin/env bash

# Note: calling Docker with PowerShell to avoid an issue with volumes on Git Bash (@2022/09)

lb_files=$(cygpath -aw $(dirname ${BASH_SOURCE[0]})/../target/liquibase-changelog/liquibase)
cmd="docker run --rm -v $lb_files/changelog:/liquibase/changelog -v $lb_files/classpath:/liquibase/classpath --add-host=bridge.docker:host-gateway liquibase/liquibase '--defaultsFile=/liquibase/changelog/liquibase.docker.properties' $1"
powershell -c $cmd
