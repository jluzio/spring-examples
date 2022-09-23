# References
- https://docs.liquibase.com/workflows/liquibase-community/using-liquibase-and-docker.html
- https://docs.liquibase.com/commands/home.html
- https://hub.docker.com/r/liquibase/liquibase

# Commands
Note: Using Bash unless stated

## Install dependencies
~~~bash
mvn clean dependency:copy -Dartifact=mysql:mysql-connector-java:8.0.30:jar -DoutputDirectory=docker/liquibase/classpath 
~~~

~~~bash
mvn -f unpack -P payment-order-service clean compile 
~~~

~~~bash
cp -R unpack/target/generated-resources/* docker/liquibase/changelog
~~~

## Liquibase commands
Note: Git Bash seems to have issues with volumes, executing using PowerBash

### Update
- Git Bash
~~~bash
win_pwd=$(cygpath -w $(pwd))
lb_files=$win_pwd/docker/liquibase
cmd="docker run --rm -v $lb_files/changelog:/liquibase/changelog -v $lb_files/classpath:/liquibase/classpath --add-host=bridge.docker:host-gateway liquibase/liquibase '--defaultsFile=/liquibase/changelog/liquibase.docker.properties' update"
powershell -c $cmd 
~~~

- Powershell
~~~bash
docker run --rm -v ${pwd}/docker/liquibase/changelog:/liquibase/changelog -v ${pwd}/docker/liquibase/classpath:/liquibase/classpath --add-host=bridge.docker:host-gateway liquibase/liquibase '--defaultsFile=/liquibase/changelog/liquibase.docker.properties' update 
~~~

### changelog-sync
- Git Bash
~~~bash
win_pwd=$(cygpath -w $(pwd))
lb_files=$win_pwd/docker/liquibase
cmd="docker run --rm -v $lb_files/changelog:/liquibase/changelog -v $lb_files/classpath:/liquibase/classpath --add-host=bridge.docker:host-gateway liquibase/liquibase '--defaultsFile=/liquibase/changelog/liquibase.docker.properties' changelog-sync"
powershell -c $cmd 
~~~
