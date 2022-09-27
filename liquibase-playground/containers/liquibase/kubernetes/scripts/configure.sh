#!/usr/bin/env bash

cd /liquibase
cp -R data my-changelog
cd my-changelog
unzip changelog.zip

cd /liquibase

alias lb="liquibase --defaultsFile=/liquibase/my-changelog/liquibase.docker.properties"
