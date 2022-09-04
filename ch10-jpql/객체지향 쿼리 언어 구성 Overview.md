# 객체지향 쿼리 언어 구성 Overview

## 1. JPQL의 특징(Java Persistence Query Language)

- 테이블이 아닌 ** 객체 ** 를 대상으로 검색하는 객체지향 쿼리
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음
- SQL 보다 간결 (엔티티 직접 조회, 묵시적 조인, 다형성 지원 )

1. H2에서 먼저 member 테이블을 생성

```sql
create table MEMBER(
  memberId bigint auto_increment primary key,
  name varchar(512) null
);
```

2. 실행 과정 살펴보기

```java
package com.example.jpql.test;  
  
import com.example.jpql.domain.Member;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class MemberTest {  
    public static void main(String[] args) {  
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
        EntityManager em = emf.createEntityManager();  
        EntityTransaction tx = em.getTransaction();  
          
        try {  
            tx.begin();  
  
            Member member = new Member();  
            member.setUsername("테스트1");  
  
            em.persist(member);  
  
            String jpql = "select m from Member as m where m.username = '테스트1'";  
            List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();  
  
            System.out.println("result : " + resultList);  
  
            tx.commit();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            em.close();  
        }  
  
    }  
}
```

```
18:22:14.316 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - parse() - HQL: select m from com.example.jpql.domain.Member as m where m.username = '테스트1'
...
( SELECT ( {select clause} member0_.memberId ) ( FromClause{level=1} Member member0_ ) ( where ( = ( member0_.name member0_.memberId username ) '테스트1' ) ) )
```

위와 같이 실제 엔티티에 매칭시켜서 쿼리를 실행시키는 것을 확인해볼 수 있다!

### 1-1. Criteria 쿼리

- ** 프로그래밍 코드로 JPQL 작성 **

ex)
```java
query.select(m).where(...)
```

> 장점은?

→  JPQL이 문자 기반 쿼리이기 때문에 오타가 있을 경우 런타임 시점에서 오류가 발생할 수 있는데, 이를 줄여줄 수 있다!
→ 동적 쿼리 작성에 편리
→ IDE에서 코드 자동완성 지원

step1 . Criteria 사용 준비

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
EntityManager em = emf.createEntityManager();  
//1. Criteria 사용 준비  
CriteriaBuilder cb = em.getCriteriaBuilder();  
CriteriaQuery<Member> query = cb.createQuery(Member.class);
```

step2. 루트 클래스(조회를 시작할 클래스)를 만들기

```java
Root<Member> m = query.from(Member.class);
```

step3. 쿼리 생성

현재 엔티티에서 name 컬럼을 "username"으로 받고 있고, name 속성의 값이 x인 경우를 조회할 것이므로 아래와 같이 될 것

```java
CriteriaQuery<Member> where = query.select(m).where(cb.equal(m.get("username"), "테스트1"));  
List<Member> resultList = em.createQuery(where).getResultList();
```

```java
package com.example.jpql.test;  
  
import com.example.jpql.domain.Member;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
import javax.persistence.criteria.CriteriaBuilder;  
import javax.persistence.criteria.CriteriaQuery;  
import javax.persistence.criteria.Root;  
import java.util.List;  
  
public class CriteriaQueryTest {  
    public static void main(String[] args) {  
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
        EntityManager em = emf.createEntityManager();  
        //1. Criteria 사용 준비  
  CriteriaBuilder cb = em.getCriteriaBuilder();  
        CriteriaQuery<Member> query = cb.createQuery(Member.class);  
  
        //2. 루트 클래스(조회를 시작할 클래스)를 만들기  
  Root<Member> m = query.from(Member.class);  
  
        //3. 쿼리 생성  
  //현재 엔티티에서 name 컬럼을 "username"으로 받고 있고, name 속성의 값이 x인 경우를 조회할 것이므로 아래와 같이 될 것  
  CriteriaQuery<Member> where = query.select(m).where(cb.equal(m.get("username"), "테스트1"));  
        List<Member> resultList = em.createQuery(where).getResultList();  
        System.out.println("result: " + resultList);  
    }  
}
```

그러면 코드 기반으로 쿼리를 살펴볼 수 있다

```
18:41:49.009 [main] DEBUG org.hibernate.hql.internal.ast.HqlSqlWalker - processQuery() :  ( SELECT ( {select clause} member0_.memberId ) ( FromClause{level=1} Member member0_ ) ( where ( = ( member0_.name member0_.memberId username ) ? ) ) )
```

그리고 지금 속성부분도 "username" 처럼 문자를 사용했는데 , ** 메타 모델 ! 메타 모델 API** 을 사용해서 코드로 고쳐볼 수 있다!

(상세 부분은 후에 정리할 것인데, "username"을 Member_.username 처럼 바꿔서 진행할 수 있다고 한다)

### 1- 2. QueryDSL

- Criteria 처럼 JPQL 빌더 역할을 수행

✅ 장점 ✅

- 코드 기반
- 단순하고 사용하기 쉬움

step1. querydsl-apt, querydsl-jpa 의존성 + 버전 을 추가해주기

```gradle
 buildscript {
    ext {
        queryDslVersion = "5.0.0"
    }
}

..

```

step2. querydsl 플러그인 추가

```
id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
```

step3. querydsl 로 쿼리를 구성하기 위한 Q클래스 구성을 위한 설정 추가

```
/**  
 * QueryDSL 설정 추가  
 *///1. Q파일 빌드 경로  
def queryDslDir = "$buildDir/generated";  
  
//2. JPA 사용 여부 및 사용 경로 설정  
querydsl{  
  jpa = true  
  querydslSourcesDir = queryDslDir;  
}  
  
//3. 빌드 시 사용할 sourceSet 추가(자바 코드를 읽어서 queryDslDir 폴더 내부에 Q파일로 저장해둘 것)  
sourceSets{  
  main.java.srcDir queryDslDir  
}  
  
//4. queryDsl이 compileClassPath를 상속하도록 설정  
configurations{  
  compileOnly{  
  extendsFrom annotationProcessor  
    }  
  querydsl.extendsFrom compileClassPath  
}
```

▶ 완성

```gradle
buildscript {  
  ext {  
  queryDslVersion = "5.0.0"  
  }  
}  
  
plugins {  
  id 'org.springframework.boot' version '2.7.3'  
  id 'io.spring.dependency-management' version '1.0.13.RELEASE'  
  id 'java'  
  id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"  
}  
  
group = 'com.example'  
version = '0.0.1-SNAPSHOT'  
sourceCompatibility = '11'  
  
  
repositories {  
  mavenCentral()  
}  
  
  
  
dependencies {  
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'  
  implementation 'org.springframework.boot:spring-boot-starter-web'  
  // https://mvnrepository.com/artifact/com.querydsl/querydsl-apt  
  implementation "com.querydsl:querydsl-apt:${queryDslVersion}"  
// https://mvnrepository.com/artifact/com.querydsl/querydsl-jpa  
  implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"  
  compileOnly 'org.projectlombok:lombok'  
  runtimeOnly 'com.h2database:h2'  
  annotationProcessor 'org.projectlombok:lombok'  
  testImplementation 'org.springframework.boot:spring-boot-starter-test'  
}  
  
tasks.named('test') {  
  useJUnitPlatform()  
}  
  
/**  
 * QueryDSL 설정 추가  
 *///1. Q파일 빌드 경로  
def queryDslDir = "$buildDir/generated";  
  
//2. JPA 사용 여부 및 사용 경로 설정  
querydsl{  
  jpa = true  
  querydslSourcesDir = queryDslDir;  
}  
  
//3. 빌드 시 사용할 sourceSet 추가(자바 코드를 읽어서 queryDslDir 폴더 내부에 Q파일로 저장해둘 것)  
sourceSets{  
  main.java.srcDir queryDslDir  
}  
  
//4. queryDsl이 compileClassPath를 상속하도록 설정  
configurations{  
  compileOnly{  
  extendsFrom annotationProcessor  
    }  
  querydsl.extendsFrom compileClassPath  
}
```
ref: https://dingdingmin-back-end-developer.tistory.com/entry/Spring-Data-JPA-7-Querydsl-%EC%82%AC%EC%9A%A9-gradle-7x

(정정) gradle 5부터는 몇가지 이슈가 있어서 아래와 같이 설정해주면 Q파일이 정상적으로 생성되는 것을 확인해볼 수 있다

```gradle
buildscript {  
  ext {  
  queryDslVersion = "5.0.0"  
  }  
}  
  
plugins {  
  id 'org.springframework.boot' version '2.7.3'  
  id 'io.spring.dependency-management' version '1.0.13.RELEASE'  
  id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"  
  id 'java'  
}  
  
group = 'com.example'  
version = '0.0.1-SNAPSHOT'  
sourceCompatibility = '11'  
  
configurations{  
  compileOnly{  
  extendsFrom annotationProcessor  
    }  
}  
  
repositories {  
  mavenCentral()  
}  
  
  
  
dependencies {  
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'  
  implementation 'org.springframework.boot:spring-boot-starter-web'  
  // https://mvnrepository.com/artifact/com.querydsl/querydsl-apt  
  implementation "com.querydsl:querydsl-apt:${queryDslVersion}"  
// https://mvnrepository.com/artifact/com.querydsl/querydsl-jpa  
  implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"  
  compileOnly 'org.projectlombok:lombok'  
  runtimeOnly 'com.h2database:h2'  
  annotationProcessor 'org.projectlombok:lombok'  
  testImplementation 'org.springframework.boot:spring-boot-starter-test'  
}  
  
tasks.named('test') {  
  useJUnitPlatform()  
}  
  
/**  
 * QueryDSL 설정 추가  
 *///1. Q파일 빌드 경로  
def queryDslDir = "$buildDir/generated/querydsl";  
  
//2. JPA 사용 여부 및 사용 경로 설정  
querydsl{  
  jpa = true  
  querydslSourcesDir = queryDslDir;  
}  
  
//3. 빌드 시 사용할 sourceSet 추가(자바 코드를 읽어서 queryDslDir 폴더 내부에 Q파일로 저장해둘 것)  
sourceSets{  
  main.java.srcDir queryDslDir  
}  
  
//4. queryDsl이 compileClassPath를 상속하도록 설정  
configurations {  
  compileOnly {  
  extendsFrom annotationProcessor  
    }  
  querydsl.extendsFrom compileClasspath  
}  
  
//Unable to load class 'com.querydsl.apt.jpa.JPAAnnotationProcessor'.  
compileQuerydsl{  
  options.annotationProcessorPath = configurations.querydsl  
}
```

ref: https://velog.io/@dbsrud11/QueryDSL-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%99%98%EA%B2%BD%EC%84%A4%EC%A0%95

나는 `Unable to load class 'com.mysema.codegen.model.Type'.`, `Unable to load class 'com.querydsl.apt.jpa.JPAAnnotationProcessor'.` 이슈가 발생했었다

step4. `JPAQuery` 와 `Q엔티티` 로  쿼리를 만들 준비해주기

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
EntityManager em = emf.createEntityManager();  
EntityTransaction tx = em.getTransaction();  
  
JPAQuery<Member> query = new JPAQuery<>(em);  
QMember qMember = QMember.member;
```

step5. 쿼리, 결과 조회

```java
List<Member> members = query.from(qMember)  
        .where(qMember.username.eq("테스트1"))  
        .fetch();
```

```java
package com.example.jpql.test;  
  
import com.example.jpql.domain.Member;  
import com.example.jpql.domain.QMember;  
import com.querydsl.jpa.impl.JPAQuery;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class QueryDslTest {  
    public static void main(String[] args) {  
        //1. 준비  
  EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
        EntityManager em = emf.createEntityManager();  
        EntityTransaction tx = em.getTransaction();  
  
        JPAQuery<Member> query = new JPAQuery<>(em);  
        QMember qMember = QMember.member;  
  
        //2. 쿼리, 결과 조회  
  List<Member> members = query.from(qMember)  
                .where(qMember.username.eq("테스트1"))  
                .fetch();  
  
        System.out.println("results : " + members);  
    }  
}
```
```
19:38:06.506 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - HQL: select member1
from com.example.jpql.domain.Member member1
where member1.username = ?1
```

그러면 코드를 기반으로 쿼리가 구성되는 것을 확인해볼 수 있다

## 2. 네이티브 SQL

- ** 특정 SQL 에서만 사용하는 SQL **
- JPQL 만으로는 한계가 있을 경우 유용
- 오라클의 CONNECT BY 가 예시가 될 수 있음
- `EntityManager인스턴스.createNativeQuery`로 접근

```java
package com.example.jpql.test;  
  
import com.example.jpql.domain.Member;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class NativeSqlTest {  
    public static void main(String[] args) {  
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
        EntityManager em = emf.createEntityManager();  
        EntityTransaction tx = em.getTransaction();  
  
        String sql = "select memberId, name from MEMBER where name = '테스트1'";  
        List<Member> members = em.createNativeQuery(sql, Member.class).getResultList();  
        System.out.println("results : " + members);  
  
    }  
}
```

## 3. JDBC(Java Database Connectivity) 직접 사용, 마이바티스 같은 SQL 매퍼 프레임워크 사용

[하이버네이트에서 직접 JDBC 커넥션을 획득하는 방법]

(JDBCTemplate을 사용해도 좋음)

```java
package com.example.jpql.test;  
  
import org.hibernate.Session;  
import org.hibernate.jdbc.Work;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.sql.Connection;  
import java.sql.ResultSet;  
import java.sql.SQLException;  
import java.sql.Statement;  
  
public class JDBCTest {  
    public static void main(String[] args) {  
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");  
        EntityManager em = emf.createEntityManager();  
        EntityTransaction tx = em.getTransaction();  
  
        Session session = em.unwrap(Session.class);  
  
        session.doWork(new Work() {  
            @Override  
  public void execute(Connection connection) throws SQLException {  
                String sql = "select * from MEMBER";  
                Statement statement = connection.createStatement();  
                statement.execute(sql);  
                ResultSet resultSet = statement.getResultSet();  
  
                while (resultSet.next()) {  
                    long memberId = resultSet.getLong(1);  
                    String name = resultSet.getString(2);  
  
                    System.out.println("member: " + memberId + " " + name);  
                }  
            }  
        });  
    }  
}
```

