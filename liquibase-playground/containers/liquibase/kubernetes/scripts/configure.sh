#!/usr/bin/env bash

cd /liquibase
cp -R data my-changelog
cd my-changelog
tar -xvf changelog.tar.gz

cd /liquibase

alias lb="/liquibase/liquibase --defaultsFile=/liquibase/my-changelog/liquibase.docker.properties"
