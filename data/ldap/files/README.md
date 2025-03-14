
~~~bash
ldapdelete -x -H ldap://localhost:1389 -D "cn=admin,dc=example,dc=org" -w adminpassword "cn=jdoe,ou=test_person_data,dc=example,dc=org"
ldapmodify -x -H ldap://localhost:1389 -D "cn=admin,dc=example,dc=org" -w adminpassword -f jdoe_modify_replace.ldif

# add
## working: add contents in stdin
ldapadd -x -H ldap://localhost:1389 -D "cn=admin,dc=example,dc=org" -w adminpassword
## requires file owned by shell user?
ldapadd -x -H ldap://localhost:1389 -D "cn=admin,dc=example,dc=org" -w adminpassword -f jdoe_load.ldif
ldapmodify -x -H ldap://localhost:1389 -D "cn=admin,dc=example,dc=org" -w adminpassword -f jdoe_load.ldif
~~~
