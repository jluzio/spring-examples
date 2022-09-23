# Commands

## List dependencies using a profile
~~~bash
mvn dependency:list -DexcludeTransitive=true -P mysql,payment-order-service
~~~

## Copy an artifact with known version
~~~bash
mvn clean dependency:copy -Dartifact=com.backbase.dbs.paymentorder:payment-order-service:1.3.161.11:jar:classes
~~~

## Copy dependencies usiong a profile
~~~bash
mvn clean dependency:copy-dependencies -DexcludeTransitive=true -P mysql,payment-order-service
~~~
