# Refs
- https://docs.spring.io/spring-boot/reference/features/ssl.html
- https://spring.io/blog/2023/06/07/securing-spring-boot-applications-with-ssl
- https://www.baeldung.com/spring-boot-security-ssl-bundles
- https://github.com/nlinhvu/spring-boot-ssl-demo-2024
- 2 way ssl: https://medium.com/@salarai.de/how-to-enable-mutual-tls-in-a-sprint-boot-application-77144047940f

# Keystore
~~~bash
# at <server>/src/main/resources/certs
# option 1
keytool -genkeypair -alias sec_svc -keyalg RSA -keysize 2048 -dname "CN=localhost, OU=jc.dev, O=jc.dev, C=PT" -validity 365 -keystore keystore.p12 -storepass s3cr3t
keytool -exportcert -alias sec_svc -keystore keystore.p12 -storepass s3cr3t -file sec_svc.crt -rfc

# option 2
openssl req -newkey rsa:2048 -x509 -sha256 -keyout sec_svc.key -out sec_svc.crt -days 365 -passout pass:s3cr3t
openssl pkcs12 -export -in sec_svc.crt -inkey sec_svc.key -out keystore.p12 -name sec_svc -passin pass:s3cr3t -passout pass:s3cr3t

# other tests
openssl req -x509 -nodes -newkey rsa:2048 -keyout sec_svc.key -out sec_svc.crt -days 365 -subj "//CN=localhost"
openssl pkcs12 -export -in sec_svc.crt -inkey sec_svc.key -out keystore.p12 -name sec_svc -passout pass:s3cr3t
~~~
