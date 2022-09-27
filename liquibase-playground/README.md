# References
- https://docs.liquibase.com/workflows/liquibase-community/using-liquibase-and-docker.html
- https://docs.liquibase.com/commands/home.html
- https://hub.docker.com/r/liquibase/liquibase

# Commands
Note: Using Bash unless stated

## Install dependencies
~~~bash
mvn -f dependencies -P changelog,copy-dependencies,backbase-app,payment-order-service, clean package 
~~~

## Prepare Liquibase files
~~~bash
mvn -f container/liquibase -P mysql clean package 
~~~

## Reference for installing other dependencies if needed
Example:
~~~bash
mvn clean dependency:copy -Dartifact=mysql:mysql-connector-java:8.0.30:jar -DoutputDirectory=containers/liquibase/target/dependency 
~~~


## Liquibase commands
Notes: 
- Git Bash seems to have issues with volumes, executing using PowerBash
- Using Rancher Desktop might require to:
  - add a firewall rule to enable host.docker.internal for accessing host services (https://docs.rancherdesktop.io/faq/#q-can-containers-reach-back-to-host-services-via-hostdockerinternal) 
  - configure an extra host on docker command to access other docker containers: --add-host=bridge.docker:host-gateway

### Docker
~~~bash
./containers/liquibase/docker/run-liquibase.sh --help
~~~

### Debug container
~~~bash
./containers/liquibase/docker/debug-container.sh
~~~

## Kubernetes
- deploy
~~~bash
./containers/liquibase/kubernetes/deploy-liquibase.sh
~~~

- run in container
~~~bash
./containers/liquibase/kubernetes/run-liquibase.sh
~~~

- undeploy
~~~bash
./containers/liquibase/kubernetes/undeploy-liquibase.sh
~~~

- create/update configmaps
~~~bash
./containers/liquibase/kubernetes/apply-configmaps.sh
~~~

### Container commands
- configure
~~~bash
. scripts/configure.sh
~~~

- liquibase with configured data
~~~bash
lb --help
~~~
