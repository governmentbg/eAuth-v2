## Вдигане на 3 различни инстанции на SP с различни нива на осигуреност

Линковете с url-те са достъпни от static/services.html

Ако портовете с които се стартират jar-те са различни(вече са заети), то трябва да се сменят и линковете. 

1.) Услуга LOW:

```bash 
 java -jar target/eauth-test-sp.jar --server.port=9092 --sp.loa=LOW --sp.service.oid=2.16.100.1.1.1.1.13.1.1.2 --sp.provider.oid=2.16.100.1.1.1.1.13 &
```
2.) Услуга SUBSTANTIAL:

```bash 
 java -jar target/eauth-test-sp.jar --server.port=9093 --sp.loa=SUBSTANTIAL --sp.service.oid=2.16.100.1.1.1.1.13.1.1.3 --sp.provider.oid=2.16.100.1.1.1.1.13 &
```
3.) Услуга HIGH:

```bash 
java -jar target/eauth-test-sp.jar --server.port=9094 --sp.loa=HIGH --sp.service.oid=2.16.100.1.1.1.1.13.1.1.4 --sp.provider.oid=2.16.100.1.1.1.1.13 &
```

#Jenkins Build:
goto: https://build.shibboleth.net/nexus/content/repositories/releases/  
export: shibboleth_net.crt

```bash   
keytool -import -trustcacerts -alias shibboleth -file shibboleth_net.crt -keystore mavenKeystore  
```
goto: https://repository.mulesoft.org/releases/  
export: *_mulesoft_org.crt  

```bash     
keytool -import -trustcacerts -alias mulesoft -file \*_mulesoft_org.crt -keystore mavenKeystore  
```
goto: https://repo.maven.apache.org/maven2/  
export: repo_maven_apache_org.crt  

```bash   
keytool -import -trustcacerts -alias maven -file repo_maven_apache_org.crt -keystore mavenKeystore  
```

#add trust store and skip admin-ui because of node unpacking
-Djavax.net.ssl.trustStore=/home/bulsiadmin/maven-certs/mavenKeystore -pl '!bg.bulsi:eauth-ui-admin'

```bash   
keytool -list -v -keystore mavenKeystore 
```

#Postgresql DB
```bash  
su - postgres 
dropdb eauth
createdb eauth -E UTF-8

pg_dump eauth -U bulsiadmin -h localhost -f eauth-backup_20200309.sql

psql -d eauth -U bulsiadmin -f /home/bulsiadmin/eauth-backup_20200309.sql -h localhost
```