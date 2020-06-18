Import `eauth-audit` module
===========================

Add **spring-boot-starter-actuator** dependency  

Add **eauth-audit** dependency

```xml
	<dependency>
		<groupId>bg.bulsi</groupId>
		<artifactId>eauth-audit</artifactId>
		<version>${project.version}</version>
	</dependency>
```

Annotate main application class:

```java
@EnableAutoConfig
```

Import `[AuditEventConfig.java](src/main/java/bg/bulsi/egov/eauth/audit/config/AuditEventConfig.java "AuditEventConfig.java")` as configuration

```java
@Configuration
@Import(AuditEventConfig.class)
```

Specify local entity and entity repository

```java
@EntityScan(basePackages = {"bg.bulsi.egov.eauth...model"})
@EnableJpaRepositories(basePackages = {"bg.bulsi.egov.eauth...repository"})
```

Update module *application.yml* with profile include:   

```yml
spring.profiles.include: actuator
```
   - copy `[application-actuator.yml](src/main/resources/application-actuator.yml "Actuator properties")` 
      & `[application-actuator-ext.properties](src/main/resources/application-actuator-ext.properties "Actuator extended properties")`
  
   - *application-actuator.yml* must exist local in module using Actuator and local setup
  
Generate custom **AuditEvent** or use *AuditApplicationEvent* constructor

Publish **AuditApplicationEvent** using *ApplicationEventPublisher*

@see `[EventTest.java](src/test/java/bg/bulsi/egov/eauth/audit/EventTest.java "Example code")`
