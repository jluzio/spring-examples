# Refs
https://docs.spring.io/spring-boot/reference/features/ssl.html
https://spring.io/blog/2023/06/07/securing-spring-boot-applications-with-ssl
https://www.baeldung.com/spring-boot-security-ssl-bundles
https://github.com/nlinhvu/spring-boot-ssl-demo-2024

# Keystore
~~~bash
# at <server>/src/main/resources/certs
# option 1
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -dname "CN=localhost, OU=jc.dev, O=jc.dev, C=PT" -validity 365 -keystore keystore.p12 -storepass s3cr3t
keytool -exportcert -alias server -keystore keystore.p12 -storepass s3cr3t -file server.crt -rfc

# option 2
openssl req -newkey rsa:2048 -x509 -sha256 -keyout server.key -out server.crt -days 365 -passout pass:s3cr3t
openssl pkcs12 -export -in server.crt -inkey server.key -out keystore.p12 -name server -passin pass:s3cr3t -passout pass:s3cr3t

# other tests
openssl req -x509 -nodes -newkey rsa:2048 -keyout server.key -out server.crt -days 365 -subj "//CN=localhost"
openssl pkcs12 -export -in server.crt -inkey server.key -out keystore.p12 -name server -passout pass:s3cr3t
~~~
