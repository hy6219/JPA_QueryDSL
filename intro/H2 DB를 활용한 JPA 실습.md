
# H2 DB를 활용한 JPA 실습

## 01. H2 DB설정

www.h2database.com에서 `All Platforms` 압축파일을 다운로드하여 압축을 풀고, bin폴더의 배치파일(h2.bat/맥은 h2.sh) 실행 후

localhost:8082 접속후

username: sa
password:
로 접속

```sql
create table member(  
    ID INTEGER CONSTRAINT PK_MEM_ID PRIMARY KEY,  
    NAME VARCHAR(255),  
    AGE INTEGER NOT NULL  
);
 ```
로 테이블 생성

## 02. `Cannot access org.springframework.context.ConfigurableApplicationContext` 에러

▶ https://nohbj.tistory.com/47

(1) main.iml 파일 삭제

(2) file-Invalidate cache and restart 누르기

* main.iml: pom.xml을 인텔리제이에서 인식하지 못해서 자체적으로 의존성 관리 등을 위해 생성하는 파일

## 03. 객체 매핑 시작

다시! member 테이블 생성

```sql
create table member(  
    ID INTEGER CONSTRAINT PK_MEM_ID PRIMARY KEY,  
    NAME VARCHAR(255),  
    AGE INTEGER NOT NULL  
);
```

엔티티 생성!!
```java
package com.example.ch02jpastart1.jpabook.start;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.*;  
  
@Table(name="member")  
@Entity  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
public class Member {  
    @Id  
 @GeneratedValue(strategy=GenerationType.IDENTITY)  
    @Column(name = "ID")  
    private Integer id;  
    @Column(name="NAME")  
    private String name;  
    @Column(name="AGE")  
    private Integer age;  
}
```

- @Table(name=~): 매핑될 테이블 이름 전달해주기
- @Entity : 이 객체를 테이블과 매핑한다고 Jpa에게 알려주기
- @Id: 기본키에 매핑
- @Column: 필드럴 컬럼에 매핑
▶ @Column이 붙지 않는다면 필드명을 사용해서 컬럼명으로 매핑(대소문자 구분하는 db 사용시, @Column(name=~)을 사용하기!

## 04. `persistence.xml` 을 사용해서 필요한 설정정보 관리하기

`META-INF/persistence.xml` 생성하기

```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">  
    <!--persistence-unit: 영속성 유닛;연동될 db 하나당 하나의 유닛으로 설정  
 unit마다 고유한 name 부여해야 함-->  
  <persistence-unit name="jpabook">  
        <properties>  
            <!--필수속성-->  
  <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>  
            <property name="javax.persistence.jdbc.user" value="sa"/>  
            <property name="javax.persistence.jdbc.password" value=""/>  
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>  
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>  
  
            <!--옵션-->  
  <property name="hibernate.show_sql" value="true"/>  
            <property name="hibernate.format_sql" value="true"/>  
        </properties>  
    </persistence-unit>  
</persistence>
```

👍 데이터베이스 방언(Dialect)

데이터베이스마다 `데이터 타입`, `다른 함수명`, `페이징 처리`가 존재하는데, 이처럼 `SQL 표준을 지키지 않거나 특정 DB만의 고유 기능`을 일컫는다!

H2: org.hibernate.dialect.H2Dialect

Oracle 11g: org.hibernate.dialect.Oracle11gDialect

MySQL8: org.hibernate.dialect.MySQL8InnoDBDialect

👍 하이버네이트 전용 속성

(1) hibernate.show_sql: 하이버네이트가 실행한 SQL 출력

(2) hibernate.format_sql: 하이버네이트가 실행한 SQL을 출력할 때 보기 쉽게 정렬

(3) hibernate.use_sql_comments: 쿼리를 출력할 때 주석도 함께 출력

(4) hibernate.id.new_generator_mappings: JPA 표준에 맞춘 새로운 키 생성 전략 사용

* persistence.xml에 `<class>jpabook.start.Member</class>`를 persistence-unit 태그 내부에 기입하게 되면, 엔티티 클래스를 인식하지 못할 경우를 대비할 수 있다!

## 05. JpaMain 클래스를 만들어서 익히기

```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class JpaMain {  
    //비즈니스 로직  
  public static void logic(EntityManager entityManager){  
        Member member=new Member();  
        member.setId(1);
        member.setAge(22);  
        member.setName("홍길동");  
  
        //등록  
  entityManager.persist(member);  
        //수정  
  member.setAge(26);  
  
        //한건 조회  
  Member findMember=entityManager.find(Member.class,1);  
        System.out.println("findMember="+findMember);  
  
        //목록조회  
  List<Member> members=entityManager.createQuery("select m from Member m",Member.class)  
                .getResultList();  
        System.out.println("members: "+members);  
  
        //삭제  
  entityManager.remove(member);  
    }  
  
    public static void main(String[] args) {  
        //1.엔티티 매니저 팩토리 생성  
  EntityManagerFactory entityManagerFactory=  
                Persistence.createEntityManagerFactory("jpabook");  
  
        //2.엔티티 매니저 생성  
  EntityManager entityManager=  
                entityManagerFactory.createEntityManager();  
  
        //3.트랜잭션 획득  
  EntityTransaction tx=  
                entityManager.getTransaction();  
  
        try {  
            //4.트랜잭션 시작  
  tx.begin();  
            //5.비즈니스 로직 실행  
  logic(entityManager);  
            //6.트랜잭션 커밋  
  tx.commit();  
        }catch (Exception e){  
            e.printStackTrace();  
        }finally {  
            //7.엔티티 매니저 종료  
  entityManager.close();  
        }  
  
        //8.엔티티 매니저 팩토리 종료  
  entityManagerFactory.close();  
    }  
}
```

👍 몇가지 짚고 넘어가보자

(1)

```java
EntityManagerFactory entityManagerFactory=  
                Persistence.createEntityManagerFactory("jpabook");  
```

/src/main/resources/META-INF/persistence.xml 에서 persistence-unit name값을 활용해서 엔티티 매니저 팩토리를 찾아오자!

- EntityFactoryManager: `애플리케이션 전체에서 딱 한번만 생성하고 공유해서 사용`해야 함!!
- EntityManager: DB에 등록/수정/삭제 가능 && `스레드 간 공유 및 재사용 권장하지 않음`(<- DB 커넥션과 밀접한 관계)


(2) EntityManager에서 수정

update(), save()를 이용하지 않아도!!
jpa는 변경 정보를 추적하기 때문에 UPDATE 쿼리가 아래와 같이 적용되어 실행될 수 있다!!

```sql
UPDATE MEMBER
SET AGE=26
WHERE ID=1;
```

(3) JPQL(Java Persistence Query Language)

```sql
List<Member> members=entityManager.createQuery("select m from Member m",Member.class)  
        .getResultList();
```

- 테이블이 아닌 엔티티 객체를 대상으로 검색하기 위해서는 DB의 모든 데이터를 애플리케이션으로 불러와서, 엔티티 객체로 변경한 다음 검색해야 하는데 이는 불가능하다! 이 문제를 검색 조건이 포함된 SQL을 활용하여 해결할 수 있다! 이것을 지원해주는 것이 바로 JPQL이다!

`JPQL vs SQL`

- JPQL: `엔티티 객체를 대상`으로 쿼리 구성
- SQL: `데이터베이스 테이블을 대상`으로 쿼리 구성

따라서 위에서 `Member`는 엔티티를 지칭하는 것이지, 절대 테이블을 지칭하지 않는다!

✅ JPQL 사용

```java
entityManager.createQuery(JPQL, 반환타입)
```
으로 쿼리 객체 생성 후, 만약 위처럼 리스트로 받기 위해서는 getResultList 메서드를 실행하면 된다!!


반드시!! 인텔리제이든 h2 서버에서든 url인 `jdbc:h2:tcp://localhost/~/test`로 접속해야함을 잊지 말자!!

JpaMain을 실행해보면 아래와 같은 결과를 얻을 수 있다

```
"C:\Program Files\Amazon Corretto\jdk1.8.0_282\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.3\lib\idea_rt.jar=9856:C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\charsets.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\cldrdata.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\dnsns.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\jaccess.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\jfxrt.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\localedata.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\nashorn.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunec.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunmscapi.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\zipfs.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jce.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jfr.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jfxswt.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jsse.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\management-agent.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\resources.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\rt.jar;D:\VirtualBox_share_folder\JpaStudy\ch02-jpa-start1\target\classes;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\2.6.2\spring-boot-starter-data-jpa-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-aop\2.6.2\spring-boot-starter-aop-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-aop\5.3.14\spring-aop-5.3.14.jar;C:\Users\gs813\.m2\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\2.6.2\spring-boot-starter-jdbc-2.6.2.jar;C:\Users\gs813\.m2\repository\com\zaxxer\HikariCP\4.0.3\HikariCP-4.0.3.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-jdbc\5.3.14\spring-jdbc-5.3.14.jar;C:\Users\gs813\.m2\repository\jakarta\transaction\jakarta.transaction-api\1.3.3\jakarta.transaction-api-1.3.3.jar;C:\Users\gs813\.m2\repository\jakarta\persistence\jakarta.persistence-api\2.2.3\jakarta.persistence-api-2.2.3.jar;C:\Users\gs813\.m2\repository\org\hibernate\hibernate-core\5.6.3.Final\hibernate-core-5.6.3.Final.jar;C:\Users\gs813\.m2\repository\org\jboss\logging\jboss-logging\3.4.2.Final\jboss-logging-3.4.2.Final.jar;C:\Users\gs813\.m2\repository\net\bytebuddy\byte-buddy\1.11.22\byte-buddy-1.11.22.jar;C:\Users\gs813\.m2\repository\antlr\antlr\2.7.7\antlr-2.7.7.jar;C:\Users\gs813\.m2\repository\org\jboss\jandex\2.2.3.Final\jandex-2.2.3.Final.jar;C:\Users\gs813\.m2\repository\com\fasterxml\classmate\1.5.1\classmate-1.5.1.jar;C:\Users\gs813\.m2\repository\org\hibernate\common\hibernate-commons-annotations\5.1.2.Final\hibernate-commons-annotations-5.1.2.Final.jar;C:\Users\gs813\.m2\repository\org\glassfish\jaxb\jaxb-runtime\2.3.5\jaxb-runtime-2.3.5.jar;C:\Users\gs813\.m2\repository\org\glassfish\jaxb\txw2\2.3.5\txw2-2.3.5.jar;C:\Users\gs813\.m2\repository\com\sun\istack\istack-commons-runtime\3.0.12\istack-commons-runtime-3.0.12.jar;C:\Users\gs813\.m2\repository\com\sun\activation\jakarta.activation\1.2.2\jakarta.activation-1.2.2.jar;C:\Users\gs813\.m2\repository\org\springframework\data\spring-data-jpa\2.6.0\spring-data-jpa-2.6.0.jar;C:\Users\gs813\.m2\repository\org\springframework\data\spring-data-commons\2.6.0\spring-data-commons-2.6.0.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-orm\5.3.14\spring-orm-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-context\5.3.14\spring-context-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-tx\5.3.14\spring-tx-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-beans\5.3.14\spring-beans-5.3.14.jar;C:\Users\gs813\.m2\repository\org\slf4j\slf4j-api\1.7.32\slf4j-api-1.7.32.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-aspects\5.3.14\spring-aspects-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.6.2\spring-boot-starter-web-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter\2.6.2\spring-boot-starter-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot\2.6.2\spring-boot-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\2.6.2\spring-boot-autoconfigure-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-logging\2.6.2\spring-boot-starter-logging-2.6.2.jar;C:\Users\gs813\.m2\repository\ch\qos\logback\logback-classic\1.2.9\logback-classic-1.2.9.jar;C:\Users\gs813\.m2\repository\ch\qos\logback\logback-core\1.2.9\logback-core-1.2.9.jar;C:\Users\gs813\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.17.0\log4j-to-slf4j-2.17.0.jar;C:\Users\gs813\.m2\repository\org\apache\logging\log4j\log4j-api\2.17.0\log4j-api-2.17.0.jar;C:\Users\gs813\.m2\repository\org\slf4j\jul-to-slf4j\1.7.32\jul-to-slf4j-1.7.32.jar;C:\Users\gs813\.m2\repository\jakarta\annotation\jakarta.annotation-api\1.3.5\jakarta.annotation-api-1.3.5.jar;C:\Users\gs813\.m2\repository\org\yaml\snakeyaml\1.29\snakeyaml-1.29.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-json\2.6.2\spring-boot-starter-json-2.6.2.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.13.1\jackson-databind-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.13.1\jackson-annotations-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.13.1\jackson-core-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.13.1\jackson-datatype-jdk8-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.13.1\jackson-datatype-jsr310-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.13.1\jackson-module-parameter-names-2.13.1.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-tomcat\2.6.2\spring-boot-starter-tomcat-2.6.2.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\9.0.56\tomcat-embed-core-9.0.56.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\9.0.56\tomcat-embed-el-9.0.56.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\9.0.56\tomcat-embed-websocket-9.0.56.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-web\5.3.14\spring-web-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-webmvc\5.3.14\spring-webmvc-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-expression\5.3.14\spring-expression-5.3.14.jar;C:\Users\gs813\.m2\repository\com\h2database\h2\1.4.200\h2-1.4.200.jar;C:\Users\gs813\.m2\repository\org\projectlombok\lombok\1.18.22\lombok-1.18.22.jar;C:\Users\gs813\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\2.3.3\jakarta.xml.bind-api-2.3.3.jar;C:\Users\gs813\.m2\repository\jakarta\activation\jakarta.activation-api\1.2.2\jakarta.activation-api-1.2.2.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-core\5.3.14\spring-core-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-jcl\5.3.14\spring-jcl-5.3.14.jar" com.example.ch02jpastart1.jpabook.start.JpaMain
23:37:56.240 [main] DEBUG org.jboss.logging - Logging Provider: org.jboss.logging.Log4j2LoggerProvider
23:37:56.379 [main] DEBUG org.hibernate.jpa.HibernatePersistenceProvider - Located and parsed 1 persistence units; checking each
23:37:56.379 [main] DEBUG org.hibernate.jpa.HibernatePersistenceProvider - Checking persistence-unit [name=jpabook, explicit-provider=null] against incoming persistence unit name [jpabook]
23:37:56.380 [main] DEBUG org.hibernate.jpa.boot.spi.ProviderChecker - No PersistenceProvider explicitly requested, assuming Hibernate
23:37:56.389 [main] DEBUG org.hibernate.jpa.internal.util.LogHelper - PersistenceUnitInfo [
	name: jpabook
	persistence provider classname: null
	classloader: null
	excludeUnlistedClasses: false
	JTA datasource: null
	Non JTA datasource: null
	Transaction type: RESOURCE_LOCAL
	PU root URL: file:/D:/VirtualBox_share_folder/JpaStudy/ch02-jpa-start1/target/classes/
	Shared Cache Mode: null
	Validation Mode: null
	Jar files URLs []
	Managed classes names [
		com.example.ch02jpastart1.jpabook.start.Member]
	Mapping files names []
	Properties [
		javax.persistence.jdbc.driver: org.h2.Driver
		javax.persistence.jdbc.password: 
		javax.persistence.jdbc.url: jdbc:h2:tcp://localhost/~/test
		hibernate.dialect: org.hibernate.dialect.H2Dialect
		hibernate.show_sql: true
		hibernate.format_sql: true
		javax.persistence.jdbc.user: sa]
23:37:56.395 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.cfg.beanvalidation.BeanValidationIntegrator].
23:37:56.398 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.secure.spi.JaccIntegrator].
23:37:56.406 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.cache.internal.CollectionCacheInvalidator].
23:37:56.482 [main] INFO org.hibernate.Version - HHH000412: Hibernate ORM core version 5.6.3.Final
23:37:56.483 [main] DEBUG org.hibernate.cfg.Environment - HHH000206: hibernate.properties not found
23:37:56.687 [main] DEBUG org.hibernate.service.spi.ServiceBinding - Overriding existing service binding [org.hibernate.secure.spi.JaccService]
23:37:56.705 [main] DEBUG org.hibernate.cache.internal.RegionFactoryInitiator - Cannot default RegionFactory based on registered strategies as `[]` RegionFactory strategies were registered
23:37:56.706 [main] DEBUG org.hibernate.cache.internal.RegionFactoryInitiator - Cache region factory : org.hibernate.cache.internal.NoCachingRegionFactory
23:37:56.728 [main] INFO org.hibernate.annotations.common.Version - HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
23:37:58.356 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration boolean -> org.hibernate.type.BooleanType@765d7657
23:37:58.356 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration boolean -> org.hibernate.type.BooleanType@765d7657
23:37:58.357 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Boolean -> org.hibernate.type.BooleanType@765d7657
23:37:58.357 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration numeric_boolean -> org.hibernate.type.NumericBooleanType@2d3379b4
23:37:58.362 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration true_false -> org.hibernate.type.TrueFalseType@6771beb3
23:37:58.363 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration yes_no -> org.hibernate.type.YesNoType@411f53a0
23:37:58.364 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte -> org.hibernate.type.ByteType@3754a4bf
23:37:58.364 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte -> org.hibernate.type.ByteType@3754a4bf
23:37:58.364 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Byte -> org.hibernate.type.ByteType@3754a4bf
23:37:58.365 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration character -> org.hibernate.type.CharacterType@5b7a5baa
23:37:58.365 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration char -> org.hibernate.type.CharacterType@5b7a5baa
23:37:58.365 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Character -> org.hibernate.type.CharacterType@5b7a5baa
23:37:58.366 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration short -> org.hibernate.type.ShortType@557caf28
23:37:58.367 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration short -> org.hibernate.type.ShortType@557caf28
23:37:58.367 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Short -> org.hibernate.type.ShortType@557caf28
23:37:58.368 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration integer -> org.hibernate.type.IntegerType@206a70ef
23:37:58.368 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration int -> org.hibernate.type.IntegerType@206a70ef
23:37:58.368 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Integer -> org.hibernate.type.IntegerType@206a70ef
23:37:58.369 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration long -> org.hibernate.type.LongType@34e9fd99
23:37:58.369 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration long -> org.hibernate.type.LongType@34e9fd99
23:37:58.370 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Long -> org.hibernate.type.LongType@34e9fd99
23:37:58.371 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration float -> org.hibernate.type.FloatType@702657cc
23:37:58.371 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration float -> org.hibernate.type.FloatType@702657cc
23:37:58.371 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Float -> org.hibernate.type.FloatType@702657cc
23:37:58.372 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration double -> org.hibernate.type.DoubleType@2d1ef81a
23:37:58.372 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration double -> org.hibernate.type.DoubleType@2d1ef81a
23:37:58.372 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Double -> org.hibernate.type.DoubleType@2d1ef81a
23:37:58.374 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration big_decimal -> org.hibernate.type.BigDecimalType@4c12331b
23:37:58.374 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.math.BigDecimal -> org.hibernate.type.BigDecimalType@4c12331b
23:37:58.376 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration big_integer -> org.hibernate.type.BigIntegerType@1573f9fc
23:37:58.376 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.math.BigInteger -> org.hibernate.type.BigIntegerType@1573f9fc
23:37:58.377 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration string -> org.hibernate.type.StringType@6a78afa0
23:37:58.377 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.String -> org.hibernate.type.StringType@6a78afa0
23:37:58.377 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration nstring -> org.hibernate.type.StringNVarcharType@10683d9d
23:37:58.378 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ncharacter -> org.hibernate.type.CharacterNCharType@6989da5e
23:37:58.379 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration url -> org.hibernate.type.UrlType@7dcf94f8
23:37:58.379 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.net.URL -> org.hibernate.type.UrlType@7dcf94f8
23:37:58.380 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Duration -> org.hibernate.type.DurationType@3eb25e1a
23:37:58.380 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.Duration -> org.hibernate.type.DurationType@3eb25e1a
23:37:58.401 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Instant -> org.hibernate.type.InstantType@4d1c00d0
23:37:58.402 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.Instant -> org.hibernate.type.InstantType@4d1c00d0
23:37:58.403 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalDateTime -> org.hibernate.type.LocalDateTimeType@302552ec
23:37:58.404 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalDateTime -> org.hibernate.type.LocalDateTimeType@302552ec
23:37:58.405 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalDate -> org.hibernate.type.LocalDateType@49438269
23:37:58.405 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalDate -> org.hibernate.type.LocalDateType@49438269
23:37:58.407 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalTime -> org.hibernate.type.LocalTimeType@1ce24091
23:37:58.407 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalTime -> org.hibernate.type.LocalTimeType@1ce24091
23:37:58.408 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration OffsetDateTime -> org.hibernate.type.OffsetDateTimeType@24aed80c
23:37:58.408 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.OffsetDateTime -> org.hibernate.type.OffsetDateTimeType@24aed80c
23:37:58.410 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration OffsetTime -> org.hibernate.type.OffsetTimeType@b3ca52e
23:37:58.410 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.OffsetTime -> org.hibernate.type.OffsetTimeType@b3ca52e
23:37:58.414 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ZonedDateTime -> org.hibernate.type.ZonedDateTimeType@d35dea7
23:37:58.415 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.ZonedDateTime -> org.hibernate.type.ZonedDateTimeType@d35dea7
23:37:58.417 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration date -> org.hibernate.type.DateType@6c40365c
23:37:58.417 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Date -> org.hibernate.type.DateType@6c40365c
23:37:58.419 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration time -> org.hibernate.type.TimeType@2df3b89c
23:37:58.420 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Time -> org.hibernate.type.TimeType@2df3b89c
23:37:58.422 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration timestamp -> org.hibernate.type.TimestampType@20d525
23:37:58.422 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Timestamp -> org.hibernate.type.TimestampType@20d525
23:37:58.422 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Date -> org.hibernate.type.TimestampType@20d525
23:37:58.423 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration dbtimestamp -> org.hibernate.type.DbTimestampType@69453e37
23:37:58.425 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar -> org.hibernate.type.CalendarType@4009e306
23:37:58.426 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Calendar -> org.hibernate.type.CalendarType@4009e306
23:37:58.426 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.GregorianCalendar -> org.hibernate.type.CalendarType@4009e306
23:37:58.427 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar_date -> org.hibernate.type.CalendarDateType@22fcf7ab
23:37:58.428 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar_time -> org.hibernate.type.CalendarTimeType@305b7c14
23:37:58.430 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration locale -> org.hibernate.type.LocaleType@17f7cd29
23:37:58.430 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Locale -> org.hibernate.type.LocaleType@17f7cd29
23:37:58.431 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration currency -> org.hibernate.type.CurrencyType@79d8407f
23:37:58.431 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Currency -> org.hibernate.type.CurrencyType@79d8407f
23:37:58.432 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration timezone -> org.hibernate.type.TimeZoneType@58a9760d
23:37:58.432 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.TimeZone -> org.hibernate.type.TimeZoneType@58a9760d
23:37:58.433 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration class -> org.hibernate.type.ClassType@65d09a04
23:37:58.434 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Class -> org.hibernate.type.ClassType@65d09a04
23:37:58.435 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration uuid-binary -> org.hibernate.type.UUIDBinaryType@6a2f6f80
23:37:58.435 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.UUID -> org.hibernate.type.UUIDBinaryType@6a2f6f80
23:37:58.436 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration uuid-char -> org.hibernate.type.UUIDCharType@291caca8
23:37:58.438 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration binary -> org.hibernate.type.BinaryType@f79e
23:37:58.438 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte[] -> org.hibernate.type.BinaryType@f79e
23:37:58.438 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [B -> org.hibernate.type.BinaryType@f79e
23:37:58.440 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration wrapper-binary -> org.hibernate.type.WrapperBinaryType@26b3fd41
23:37:58.440 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Byte[] -> org.hibernate.type.WrapperBinaryType@26b3fd41
23:37:58.440 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [Ljava.lang.Byte; -> org.hibernate.type.WrapperBinaryType@26b3fd41
23:37:58.441 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration row_version -> org.hibernate.type.RowVersionType@445b295b
23:37:58.442 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration image -> org.hibernate.type.ImageType@757277dc
23:37:58.443 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration characters -> org.hibernate.type.CharArrayType@10aa41f2
23:37:58.444 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration char[] -> org.hibernate.type.CharArrayType@10aa41f2
23:37:58.444 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [C -> org.hibernate.type.CharArrayType@10aa41f2
23:37:58.445 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration wrapper-characters -> org.hibernate.type.CharacterArrayType@38102d01
23:37:58.445 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [Ljava.lang.Character; -> org.hibernate.type.CharacterArrayType@38102d01
23:37:58.445 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Character[] -> org.hibernate.type.CharacterArrayType@38102d01
23:37:58.446 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration text -> org.hibernate.type.TextType@4e3958e7
23:37:58.446 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ntext -> org.hibernate.type.NTextType@5c90e579
23:37:58.448 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration blob -> org.hibernate.type.BlobType@2d2ffcb7
23:37:58.448 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Blob -> org.hibernate.type.BlobType@2d2ffcb7
23:37:58.448 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_blob -> org.hibernate.type.MaterializedBlobType@424e1977
23:37:58.450 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration clob -> org.hibernate.type.ClobType@3c0be339
23:37:58.450 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Clob -> org.hibernate.type.ClobType@3c0be339
23:37:58.452 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration nclob -> org.hibernate.type.NClobType@68267da0
23:37:58.452 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.NClob -> org.hibernate.type.NClobType@68267da0
23:37:58.453 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_clob -> org.hibernate.type.MaterializedClobType@6a2b953e
23:37:58.454 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_nclob -> org.hibernate.type.MaterializedNClobType@548e6d58
23:37:58.456 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration serializable -> org.hibernate.type.SerializableType@21b2e768
23:37:58.459 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration object -> org.hibernate.type.ObjectType@2b4bac49
23:37:58.459 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Object -> org.hibernate.type.ObjectType@2b4bac49
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_date -> org.hibernate.type.AdaptedImmutableType@3e96bacf
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_time -> org.hibernate.type.AdaptedImmutableType@484970b0
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_timestamp -> org.hibernate.type.AdaptedImmutableType@4470f8a6
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_dbtimestamp -> org.hibernate.type.AdaptedImmutableType@7c83dc97
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_calendar -> org.hibernate.type.AdaptedImmutableType@7748410a
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_calendar_date -> org.hibernate.type.AdaptedImmutableType@740773a3
23:37:58.460 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_binary -> org.hibernate.type.AdaptedImmutableType@37f1104d
23:37:58.461 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_serializable -> org.hibernate.type.AdaptedImmutableType@55740540
23:37:58.472 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting ScanEnvironment [org.hibernate.jpa.boot.internal.StandardJpaScanEnvironmentImpl@524d6d96] into BootstrapContext; was [null]
23:37:58.473 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting ScanOptions [org.hibernate.boot.archive.scan.internal.StandardScanOptions@152aa092] into BootstrapContext; was [org.hibernate.boot.archive.scan.internal.StandardScanOptions@44a7bfbc]
23:37:58.552 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting JPA temp ClassLoader [null] into BootstrapContext; was [null]
23:37:58.552 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - ClassLoaderAccessImpl#injectTempClassLoader(null) [was null]
23:37:58.565 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid2] -> [org.hibernate.id.UUIDGenerator]
23:37:58.566 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [guid] -> [org.hibernate.id.GUIDGenerator]
23:37:58.566 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid] -> [org.hibernate.id.UUIDHexGenerator]
23:37:58.566 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid.hex] -> [org.hibernate.id.UUIDHexGenerator]
23:37:58.567 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [assigned] -> [org.hibernate.id.Assigned]
23:37:58.567 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [identity] -> [org.hibernate.id.IdentityGenerator]
23:37:58.568 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [select] -> [org.hibernate.id.SelectGenerator]
23:37:58.569 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [sequence] -> [org.hibernate.id.enhanced.SequenceStyleGenerator]
23:37:58.571 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [seqhilo] -> [org.hibernate.id.SequenceHiLoGenerator]
23:37:58.571 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [increment] -> [org.hibernate.id.IncrementGenerator]
23:37:58.572 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [foreign] -> [org.hibernate.id.ForeignGenerator]
23:37:58.572 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [sequence-identity] -> [org.hibernate.id.SequenceIdentityGenerator]
23:37:58.573 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [enhanced-sequence] -> [org.hibernate.id.enhanced.SequenceStyleGenerator]
23:37:58.573 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [enhanced-table] -> [org.hibernate.id.enhanced.TableGenerator]
23:37:58.579 [main] WARN org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
23:37:58.582 [main] INFO org.hibernate.orm.connections.pooling - HHH10001005: using driver [org.h2.Driver] at URL [jdbc:h2:tcp://localhost/~/test]
23:37:58.583 [main] INFO org.hibernate.orm.connections.pooling - HHH10001001: Connection properties: {user=sa, password=}
23:37:58.583 [main] INFO org.hibernate.orm.connections.pooling - HHH10001003: Autocommit mode: false
23:37:58.587 [main] DEBUG org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl - Initializing Connection pool with 1 Connections
23:37:58.587 [main] INFO org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl - HHH000115: Hibernate connection pool size: 20 (min=1)
23:37:58.674 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - Database ->
       name : H2
    version : 1.4.200 (2019-10-14)
      major : 1
      minor : 4
23:37:58.675 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - Driver ->
       name : H2 JDBC Driver
    version : 1.4.200 (2019-10-14)
      major : 1
      minor : 4
23:37:58.675 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - JDBC version : 4.1
23:37:58.688 [main] INFO org.hibernate.dialect.Dialect - HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
23:37:58.736 [main] DEBUG org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder - JDBC driver metadata reported database stores quoted identifiers in neither upper, lower nor mixed case
23:37:58.760 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration@41f69e84] to MetadataBuildingContext [org.hibernate.boot.internal.MetadataBuildingContextRootImpl@7975d1d8]
23:37:58.803 [main] DEBUG org.hibernate.boot.model.relational.Namespace - Created database namespace [logicalName=Name{catalog=null, schema=null}, physicalName=Name{catalog=null, schema=null}]
23:37:58.818 [main] DEBUG org.hibernate.cfg.AnnotationBinder - Binding entity from annotated class: com.example.ch02jpastart1.jpabook.start.Member
23:37:58.840 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3DiscriminatorColumn{logicalColumnName'DTYPE', discriminatorTypeName='string'}
23:37:58.846 [main] DEBUG org.hibernate.cfg.annotations.EntityBinder - Import with entity name Member
23:37:58.850 [main] DEBUG org.hibernate.cfg.annotations.EntityBinder - Bind entity com.example.ch02jpastart1.jpabook.start.Member on table member
23:37:58.894 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=ID, insertable=true, updatable=true, unique=false}
23:37:58.927 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - Not known whether passed class name [com.example.ch02jpastart1.jpabook.start.Member] is safe
23:37:58.928 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - No temp ClassLoader provided; using live ClassLoader for loading potentially unsafe class : com.example.ch02jpastart1.jpabook.start.Member
23:37:58.928 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property id with lazy=false
23:37:58.931 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:id]
23:37:58.934 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for id
23:37:58.936 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property id
23:37:58.942 [main] DEBUG org.hibernate.cfg.BinderHelper - #makeIdGenerator(org.hibernate.mapping.SimpleValue([org.hibernate.mapping.Column(ID)]), id, assigned, , ...)
23:37:58.943 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=AGE, insertable=true, updatable=true, unique=false}
23:37:58.943 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property age with lazy=false
23:37:58.943 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:age]
23:37:58.943 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for age
23:37:58.943 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property age
23:37:58.944 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=NAME, insertable=true, updatable=true, unique=false}
23:37:58.944 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property name with lazy=false
23:37:58.944 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:name]
23:37:58.944 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for name
23:37:58.945 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property name
23:37:58.962 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for id
23:37:58.962 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for age
23:37:58.962 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for name
23:37:58.963 [main] DEBUG org.hibernate.mapping.PrimaryKey - Forcing column [id] to be non-null as it is part of the primary key for table [member]
23:37:59.013 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Building session factory
23:37:59.014 [main] DEBUG org.hibernate.cfg.Settings - SessionFactory name : null
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Automatic flush during beforeCompletion(): enabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Automatic session close at end of transaction: disabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Statistics: disabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Deleted entity synthetic identifier rollback: disabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Default entity-mode: pojo
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Check Nullability in Core (should be disabled when Bean Validation is on): enabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Allow initialization of lazy state outside session : disabled
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Using BatchFetchStyle : LEGACY
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Default batch fetch size: -1
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Maximum outer join fetch depth: null
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Default null ordering: NONE
23:37:59.015 [main] DEBUG org.hibernate.cfg.Settings - Order SQL updates by primary key: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Order SQL inserts for batching: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - multi-tenancy strategy : NONE
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - JTA Track by Thread: enabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Query language substitutions: {}
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Named query checking : enabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Second-level query cache: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Second-level query cache factory: null
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache region prefix: null
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Optimize second-level cache for minimal puts: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Structured second-level cache entries: disabled
23:37:59.016 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache direct-reference entries: disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - Automatic eviction of collection cache: disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JDBC batch size: 15
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JDBC batch updates for versioned data: enabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - Scrollable result sets: enabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - Wrap result sets: disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JDBC3 getGeneratedKeys(): enabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JDBC result set fetch size: null
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - Connection release mode: AFTER_TRANSACTION
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - Generate SQL with comments: disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - query : disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - closed-handling : disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - lists : disabled
23:37:59.017 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - transactions : disabled
23:37:59.074 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
23:37:59.075 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
23:37:59.090 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Session factory constructed with filter configurations : {}
23:37:59.090 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Instantiating session factory with properties: {sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, hibernate.format_sql=true, java.specification.version=1.8, file.encoding.pkg=sun.io, sun.cpu.isalist=amd64, sun.jnu.encoding=MS949, hibernate.dialect=org.hibernate.dialect.H2Dialect, java.class.path=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\charsets.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\cldrdata.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\dnsns.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\jaccess.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\jfxrt.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\localedata.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\nashorn.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunec.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunmscapi.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext\zipfs.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jce.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jfr.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jfxswt.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jsse.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\management-agent.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\resources.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\rt.jar;D:\VirtualBox_share_folder\JpaStudy\ch02-jpa-start1\target\classes;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\2.6.2\spring-boot-starter-data-jpa-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-aop\2.6.2\spring-boot-starter-aop-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-aop\5.3.14\spring-aop-5.3.14.jar;C:\Users\gs813\.m2\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\2.6.2\spring-boot-starter-jdbc-2.6.2.jar;C:\Users\gs813\.m2\repository\com\zaxxer\HikariCP\4.0.3\HikariCP-4.0.3.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-jdbc\5.3.14\spring-jdbc-5.3.14.jar;C:\Users\gs813\.m2\repository\jakarta\transaction\jakarta.transaction-api\1.3.3\jakarta.transaction-api-1.3.3.jar;C:\Users\gs813\.m2\repository\jakarta\persistence\jakarta.persistence-api\2.2.3\jakarta.persistence-api-2.2.3.jar;C:\Users\gs813\.m2\repository\org\hibernate\hibernate-core\5.6.3.Final\hibernate-core-5.6.3.Final.jar;C:\Users\gs813\.m2\repository\org\jboss\logging\jboss-logging\3.4.2.Final\jboss-logging-3.4.2.Final.jar;C:\Users\gs813\.m2\repository\net\bytebuddy\byte-buddy\1.11.22\byte-buddy-1.11.22.jar;C:\Users\gs813\.m2\repository\antlr\antlr\2.7.7\antlr-2.7.7.jar;C:\Users\gs813\.m2\repository\org\jboss\jandex\2.2.3.Final\jandex-2.2.3.Final.jar;C:\Users\gs813\.m2\repository\com\fasterxml\classmate\1.5.1\classmate-1.5.1.jar;C:\Users\gs813\.m2\repository\org\hibernate\common\hibernate-commons-annotations\5.1.2.Final\hibernate-commons-annotations-5.1.2.Final.jar;C:\Users\gs813\.m2\repository\org\glassfish\jaxb\jaxb-runtime\2.3.5\jaxb-runtime-2.3.5.jar;C:\Users\gs813\.m2\repository\org\glassfish\jaxb\txw2\2.3.5\txw2-2.3.5.jar;C:\Users\gs813\.m2\repository\com\sun\istack\istack-commons-runtime\3.0.12\istack-commons-runtime-3.0.12.jar;C:\Users\gs813\.m2\repository\com\sun\activation\jakarta.activation\1.2.2\jakarta.activation-1.2.2.jar;C:\Users\gs813\.m2\repository\org\springframework\data\spring-data-jpa\2.6.0\spring-data-jpa-2.6.0.jar;C:\Users\gs813\.m2\repository\org\springframework\data\spring-data-commons\2.6.0\spring-data-commons-2.6.0.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-orm\5.3.14\spring-orm-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-context\5.3.14\spring-context-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-tx\5.3.14\spring-tx-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-beans\5.3.14\spring-beans-5.3.14.jar;C:\Users\gs813\.m2\repository\org\slf4j\slf4j-api\1.7.32\slf4j-api-1.7.32.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-aspects\5.3.14\spring-aspects-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.6.2\spring-boot-starter-web-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter\2.6.2\spring-boot-starter-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot\2.6.2\spring-boot-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\2.6.2\spring-boot-autoconfigure-2.6.2.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-logging\2.6.2\spring-boot-starter-logging-2.6.2.jar;C:\Users\gs813\.m2\repository\ch\qos\logback\logback-classic\1.2.9\logback-classic-1.2.9.jar;C:\Users\gs813\.m2\repository\ch\qos\logback\logback-core\1.2.9\logback-core-1.2.9.jar;C:\Users\gs813\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.17.0\log4j-to-slf4j-2.17.0.jar;C:\Users\gs813\.m2\repository\org\apache\logging\log4j\log4j-api\2.17.0\log4j-api-2.17.0.jar;C:\Users\gs813\.m2\repository\org\slf4j\jul-to-slf4j\1.7.32\jul-to-slf4j-1.7.32.jar;C:\Users\gs813\.m2\repository\jakarta\annotation\jakarta.annotation-api\1.3.5\jakarta.annotation-api-1.3.5.jar;C:\Users\gs813\.m2\repository\org\yaml\snakeyaml\1.29\snakeyaml-1.29.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-json\2.6.2\spring-boot-starter-json-2.6.2.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.13.1\jackson-databind-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.13.1\jackson-annotations-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.13.1\jackson-core-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.13.1\jackson-datatype-jdk8-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.13.1\jackson-datatype-jsr310-2.13.1.jar;C:\Users\gs813\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.13.1\jackson-module-parameter-names-2.13.1.jar;C:\Users\gs813\.m2\repository\org\springframework\boot\spring-boot-starter-tomcat\2.6.2\spring-boot-starter-tomcat-2.6.2.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\9.0.56\tomcat-embed-core-9.0.56.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\9.0.56\tomcat-embed-el-9.0.56.jar;C:\Users\gs813\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\9.0.56\tomcat-embed-websocket-9.0.56.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-web\5.3.14\spring-web-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-webmvc\5.3.14\spring-webmvc-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-expression\5.3.14\spring-expression-5.3.14.jar;C:\Users\gs813\.m2\repository\com\h2database\h2\1.4.200\h2-1.4.200.jar;C:\Users\gs813\.m2\repository\org\projectlombok\lombok\1.18.22\lombok-1.18.22.jar;C:\Users\gs813\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\2.3.3\jakarta.xml.bind-api-2.3.3.jar;C:\Users\gs813\.m2\repository\jakarta\activation\jakarta.activation-api\1.2.2\jakarta.activation-api-1.2.2.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-core\5.3.14\spring-core-5.3.14.jar;C:\Users\gs813\.m2\repository\org\springframework\spring-jcl\5.3.14\spring-jcl-5.3.14.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.3\lib\idea_rt.jar, java.vm.vendor=Amazon.com Inc., sun.arch.data.model=64, user.variant=, java.vendor.url=https://aws.amazon.com/corretto/, user.timezone=Asia/Seoul, javax.persistence.jdbc.url=jdbc:h2:tcp://localhost/~/test, javax.persistence.jdbc.user=****, os.name=Windows 10, java.vm.specification.version=1.8, jakarta.persistence.jdbc.password=****, user.country=KR, sun.java.launcher=SUN_STANDARD, local.setting.IS_JTA_TXN_COORD=false, sun.boot.library.path=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\bin, sun.java.command=com.example.ch02jpastart1.jpabook.start.JpaMain, jakarta.persistence.jdbc.driver=org.h2.Driver, sun.cpu.endian=little, user.home=C:\Users\gs813, user.language=ko, java.specification.vendor=Oracle Corporation, java.home=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre, file.separator=\, jakarta.persistence.jdbc.user=****, line.separator=
, hibernate.persistenceUnitName=jpabook, java.vm.specification.vendor=Oracle Corporation, java.specification.name=Java Platform API Specification, hibernate.transaction.coordinator_class=class org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, javax.persistence.jdbc.driver=org.h2.Driver, sun.boot.class.path=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\resources.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\rt.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\sunrsasign.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jsse.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jce.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\charsets.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\jfr.jar;C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\classes, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=1.8.0_282-b08, user.name=gs813, path.separator=;, hibernate.connection.username=****, os.version=10.0, java.endorsed.dirs=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\endorsed, java.runtime.name=OpenJDK Runtime Environment, hibernate.connection.url=jdbc:h2:tcp://localhost/~/test, file.encoding=UTF-8, hibernate.ejb.persistenceUnitName=jpabook, java.vm.name=OpenJDK 64-Bit Server VM, hibernate.show_sql=true, hibernate.connection.driver_class=org.h2.Driver, java.vendor.url.bug=https://github.com/corretto/corretto-8/issues/, java.io.tmpdir=C:\Users\gs813\AppData\Local\Temp\, java.version=1.8.0_282, user.dir=D:\VirtualBox_share_folder\JpaStudy\ch02-jpa-start1, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, hibernate.connection.password=****, sun.os.patch.level=, jakarta.persistence.jdbc.url=jdbc:h2:tcp://localhost/~/test, hibernate.boot.CfgXmlAccessService.key=org.hibernate.boot.registry.StandardServiceRegistryBuilder$1@3700ec9c, java.library.path=C:\Program Files\Amazon Corretto\jdk1.8.0_282\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\Program Files\Amazon Corretto\jdk11.0.13_8\bin;C:\oraclexe\app\oracle\product\11.2.0\server\bin;C:\oraclexe\app\oracle\product\11.2.0\server\bin;C:\Python39\Scripts\;C:\Python39\;C:\Program Files (x86)\Windows Resource Kits\Tools\;C:\Program Files\Amazon Corretto\jdk1.8.0_282\bin;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\iCLS\;C:\Program Files\Intel\Intel(R) Management Engine Components\iCLS\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\OpenSSH\;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;C:\Users\gs813\AppData\Local\Android\Sdk\platform-tools;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Git\cmd;C:\Program Files\nodejs\;C:\ProgramData\chocolatey\bin;%GRADLE_HOME%\bin;C:\MinGW\bin;C:\Program Files\MySQL\MySQL Shell 8.0\bin\;C:\Ruby25-x64\bin;C:\Users\gs813\AppData\Local\Microsoft\WindowsApps;C:\Users\gs813\AppData\Local\GitHubDesktop\bin;C:\Users\gs813\AppData\Local\Programs\Microsoft VS Code\bin;C:\Program Files\Docker Toolbox;C:\Program Files\JetBrains\CLion 2019.3.4\bin;C:\Program Files\JetBrains\IntelliJ IDEA 2020.1\bin;C:\Users\gs813\AppData\Local\atom\bin;C:\Users\gs813\AppData\Local\Microsoft\WindowsApps;C:\Users\gs813\AppData\Roaming\npm;C:\Program Files\Bandizip\;;., java.vendor=Amazon.com Inc., java.vm.info=mixed mode, java.vm.version=25.282-b08, hibernate.bytecode.use_reflection_optimizer=false, sun.io.unicode.encoding=UnicodeLittle, java.ext.dirs=C:\Program Files\Amazon Corretto\jdk1.8.0_282\jre\lib\ext;C:\WINDOWS\Sun\Java\lib\ext, javax.persistence.jdbc.password=****, java.class.version=52.0}
23:37:59.100 [main] DEBUG org.hibernate.secure.spi.JaccIntegrator - Skipping JACC integration as it was not enabled
23:37:59.101 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Instantiated session factory
23:37:59.102 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration@41f69e84] to SessionFactoryImpl [org.hibernate.internal.SessionFactoryImpl@4b2a01d4]
23:37:59.150 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - Not known whether passed class name [com.example.ch02jpastart1.jpabook.start.Member] is safe
23:37:59.151 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - No temp ClassLoader provided; using live ClassLoader for loading potentially unsafe class : com.example.ch02jpastart1.jpabook.start.Member
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister - Static SQL for entity: com.example.ch02jpastart1.jpabook.start.Member
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Version select: select ID from member where ID =?
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Snapshot select: select member_.ID, member_.AGE as age2_0_, member_.NAME as name3_0_ from member member_ where member_.ID=?
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Insert 0: insert into member (AGE, NAME, ID) values (?, ?, ?)
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Update 0: update member set AGE=?, NAME=? where ID=?
23:37:59.386 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Delete 0: delete from member where ID=?
23:37:59.425 [main] DEBUG org.hibernate.loader.plan.build.internal.spaces.QuerySpacesImpl - Adding QuerySpace : uid = <gen:0> -> org.hibernate.loader.plan.build.internal.spaces.EntityQuerySpaceImpl@5ca17ab0]
23:37:59.426 [main] DEBUG org.hibernate.persister.walking.spi.MetamodelGraphWalker - Visiting attribute path : age
23:37:59.426 [main] DEBUG org.hibernate.persister.walking.spi.MetamodelGraphWalker - Visiting attribute path : name
23:37:59.426 [main] DEBUG org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy - Building LoadPlan...
23:37:59.436 [main] DEBUG org.hibernate.loader.plan.exec.internal.LoadQueryJoinAndFetchProcessor - processing queryspace <gen:0>
23:37:59.441 [main] DEBUG org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter - LoadPlan(entity=com.example.ch02jpastart1.jpabook.start.Member)
    - Returns
       - EntityReturnImpl(entity=com.example.ch02jpastart1.jpabook.start.Member, querySpaceUid=<gen:0>, path=com.example.ch02jpastart1.jpabook.start.Member)
    - QuerySpaces
       - EntityQuerySpaceImpl(uid=<gen:0>, entity=com.example.ch02jpastart1.jpabook.start.Member)
          - SQL table alias mapping - member0_
          - alias suffix - 0_
          - suffixed key columns - {id1_0_0_}

23:37:59.442 [main] DEBUG org.hibernate.loader.entity.plan.EntityLoader - Static select for entity com.example.ch02jpastart1.jpabook.start.Member [NONE]: select member0_.ID as id1_0_0_, member0_.AGE as age2_0_0_, member0_.NAME as name3_0_0_ from member member0_ where member0_.ID=?
23:37:59.471 [main] DEBUG org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator - No actions specified; doing nothing
23:37:59.471 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator - No JtaPlatform was specified, checking resolver
23:37:59.472 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformResolverInitiator - No JtaPlatformResolver was specified, using default [org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver]
23:37:59.477 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver - Could not resolve JtaPlatform, using default [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
23:37:59.477 [main] INFO org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
23:37:59.478 [main] DEBUG org.hibernate.query.spi.NamedQueryRepository - Checking 0 named HQL queries
23:37:59.478 [main] DEBUG org.hibernate.query.spi.NamedQueryRepository - Checking 0 named SQL queries
23:37:59.483 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
23:37:59.488 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Initializing SessionFactoryRegistry : org.hibernate.internal.SessionFactoryRegistry@6f0628de
23:37:59.490 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Registering SessionFactory: 7a370fb8-d6dc-4589-9d5a-fcfe74ed1735 (<unnamed>)
23:37:59.490 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Not binding SessionFactory to JNDI, no JNDI name configured
23:37:59.549 [main] DEBUG org.hibernate.stat.internal.StatisticsInitiator - Statistics initialized [enabled=false]
23:37:59.554 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == false
23:37:59.554 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - begin
23:37:59.560 [main] DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: 1, using strategy: org.hibernate.id.Assigned
findMember=Member(id=1, name=홍길동, age=26)
23:37:59.592 [main] DEBUG org.hibernate.hql.internal.QueryTranslatorFactoryInitiator - QueryTranslatorFactory: org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory@325f7fa9
23:37:59.620 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - parse() - HQL: select m from com.example.ch02jpastart1.jpabook.start.Member m
23:37:59.625 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - Keyword  'Member' is being interpreted as an identifier due to: expecting IDENT, found 'Member'
23:37:59.629 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
23:37:59.638 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - --- HQL AST ---
 \-[QUERY] Node: 'query'
    \-[SELECT_FROM] Node: 'SELECT_FROM'
       +-[FROM] Node: 'from'
       |  \-[RANGE] Node: 'RANGE'
       |     +-[DOT] Node: '.'
       |     |  +-[DOT] Node: '.'
       |     |  |  +-[DOT] Node: '.'
       |     |  |  |  +-[DOT] Node: '.'
       |     |  |  |  |  +-[DOT] Node: '.'
       |     |  |  |  |  |  +-[IDENT] Node: 'com'
       |     |  |  |  |  |  \-[IDENT] Node: 'example'
       |     |  |  |  |  \-[IDENT] Node: 'ch02jpastart1'
       |     |  |  |  \-[IDENT] Node: 'jpabook'
       |     |  |  \-[IDENT] Node: 'start'
       |     |  \-[WEIRD_IDENT] Node: 'Member'
       |     \-[ALIAS] Node: 'm'
       \-[SELECT] Node: 'select'
          \-[IDENT] Node: 'm'

23:37:59.664 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select << begin [level=1, statement=select]
23:37:59.684 [main] DEBUG org.hibernate.hql.internal.ast.tree.FromElement - FromClause{level=1} : com.example.ch02jpastart1.jpabook.start.Member (m) -> member0_
23:37:59.685 [main] DEBUG org.hibernate.hql.internal.ast.tree.FromReferenceNode - Resolved : m -> member0_.ID
23:37:59.687 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select : finishing up [level=1, statement=select]
23:37:59.687 [main] DEBUG org.hibernate.hql.internal.ast.HqlSqlWalker - processQuery() :  ( SELECT ( {select clause} member0_.ID ) ( FromClause{level=1} member member0_ ) )
23:37:59.696 [main] DEBUG org.hibernate.hql.internal.ast.util.JoinProcessor - Tables referenced from query nodes:
 \-QueryNode
    +-SelectClause
    | referencedTables(entity Member): [member]
    |  +-IdentNode
    |  | persister: SingleTableEntityPersister(com.example.ch02jpastart1.jpabook.start.Member)
    |  | originalText: m
    |  \-SqlFragment
    \-FromClause
       \-FromElement

23:37:59.701 [main] DEBUG org.hibernate.hql.internal.ast.util.JoinProcessor - Using FROM fragment [member member0_]
23:37:59.701 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select >> end [level=1, statement=select]
23:37:59.702 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - --- SQL AST ---
 \-[SELECT] QueryNode: 'SELECT'  querySpaces (member)
    +-[SELECT_CLAUSE] SelectClause: '{select clause}'
    |  +-[ALIAS_REF] IdentNode: 'member0_.ID as id1_0_' {alias=m, className=com.example.ch02jpastart1.jpabook.start.Member, tableAlias=member0_}
    |  \-[SQL_TOKEN] SqlFragment: 'member0_.AGE as age2_0_, member0_.NAME as name3_0_'
    \-[FROM] FromClause: 'from' FromClause{level=1, fromElementCounter=1, fromElements=1, fromElementByClassAlias=[m], fromElementByTableAlias=[member0_], fromElementsByPath=[], collectionJoinFromElementsByPath=[], impliedElements=[]}
       \-[FROM_FRAGMENT] FromElement: 'member member0_' FromElement{explicit,not a collection join,not a fetch join,fetch non-lazy properties,classAlias=m,role=null,tableName=member,tableAlias=member0_,origin=null,columns={,className=com.example.ch02jpastart1.jpabook.start.Member}}

23:37:59.702 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
23:37:59.714 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - HQL: select m from com.example.ch02jpastart1.jpabook.start.Member m
23:37:59.714 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - SQL: select member0_.ID as id1_0_, member0_.AGE as age2_0_, member0_.NAME as name3_0_ from member member0_
23:37:59.714 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
23:37:59.732 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Processing flush-time cascades
23:37:59.734 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Dirty checking collections
23:37:59.743 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 1 insertions, 1 updates, 0 deletions to 1 objects
23:37:59.743 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 (re)creations, 0 updates, 0 removals to 0 collections
23:37:59.744 [main] DEBUG org.hibernate.internal.util.EntityPrinter - Listing entities:
23:37:59.744 [main] DEBUG org.hibernate.internal.util.EntityPrinter - com.example.ch02jpastart1.jpabook.start.Member{name=홍길동, id=1, age=26}
23:37:59.745 [main] DEBUG org.hibernate.engine.spi.ActionQueue - Changes must be flushed to space: member
23:37:59.759 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        member
        (AGE, NAME, ID) 
    values
        (?, ?, ?)
Hibernate: 
    insert 
    into
        member
        (AGE, NAME, ID) 
    values
        (?, ?, ?)
23:37:59.768 [main] DEBUG org.hibernate.SQL - 
    update
        member 
    set
        AGE=?,
        NAME=? 
    where
        ID=?
Hibernate: 
    update
        member 
    set
        AGE=?,
        NAME=? 
    where
        ID=?
23:37:59.780 [main] DEBUG org.hibernate.SQL - 
    select
        member0_.ID as id1_0_,
        member0_.AGE as age2_0_,
        member0_.NAME as name3_0_ 
    from
        member member0_
Hibernate: 
    select
        member0_.ID as id1_0_,
        member0_.AGE as age2_0_,
        member0_.NAME as name3_0_ 
    from
        member member0_
23:37:59.783 [main] DEBUG org.hibernate.loader.Loader - Result set row: 0
23:37:59.787 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#1]
members: [Member(id=1, name=홍길동, age=26)]
23:37:59.809 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - committing
23:37:59.810 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Processing flush-time cascades
23:37:59.811 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Dirty checking collections
23:37:59.811 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 insertions, 0 updates, 1 deletions to 1 objects
23:37:59.811 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 (re)creations, 0 updates, 0 removals to 0 collections
23:37:59.811 [main] DEBUG org.hibernate.internal.util.EntityPrinter - Listing entities:
23:37:59.811 [main] DEBUG org.hibernate.internal.util.EntityPrinter - com.example.ch02jpastart1.jpabook.start.Member{name=홍길동, id=1, age=26}
23:37:59.812 [main] DEBUG org.hibernate.SQL - 
    delete 
    from
        member 
    where
        ID=?
Hibernate: 
    delete 
    from
        member 
    where
        ID=?
23:37:59.817 [main] DEBUG org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl - Initiating JDBC connection release from afterTransaction
23:37:59.818 [main] DEBUG org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl - Initiating JDBC connection release from afterTransaction
23:37:59.818 [main] DEBUG org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl - HHH000420: Closing un-released batch
23:37:59.819 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - HHH000031: Closing
23:37:59.819 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Un-scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration$Scope@6ef7623] from SessionFactory [org.hibernate.internal.SessionFactoryImpl@4b2a01d4]
23:37:59.819 [main] DEBUG org.hibernate.service.internal.AbstractServiceRegistryImpl - Implicitly destroying ServiceRegistry on de-registration of all child ServiceRegistries
23:37:59.819 [main] INFO org.hibernate.orm.connections.pooling - HHH10001008: Cleaning up connection pool [jdbc:h2:tcp://localhost/~/test]
23:37:59.823 [main] DEBUG org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl - Implicitly destroying Boot-strap registry on de-registration of all child ServiceRegistries

Process finished with exit code 0


```
