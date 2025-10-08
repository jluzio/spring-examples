# cli

## Using h2
- start
~~~bash
liquibase init start-h2
~~~

- properties
~~~properties
url=jdbc:h2:tcp://localhost:9090/mem:dev
username=sa
password=letmein
~~~

## execute-sql
~~~bash
liquibase execute-sql (--sql | --sql-file)
~~~

## Using liquibase-maven-plugin
Note: classpath is defined by the plugin declaration, so it's easier to include JDBC Drivers.

~~~bash
mvn liquibase:updateSQL -Dliquibase.propertyFile=liquibase-multiple-using-maven-plugin.properties
~~~
