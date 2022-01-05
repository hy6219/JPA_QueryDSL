# 영속성 관리

## 01. 영속성 컨텍스트

- 엔티티를 영구 저장하는 환경
- 엔티티에 대해서 조회/수정/삭제/갱신 작업을 할 경우 엔티티 매니저가 엔티티를 저장하는 공간!!

## 02. 엔티티 생명주기

![엔티티 생명주기](https://3.bp.blogspot.com/-_7ZSlJmwcxk/XC4PU_L4BfI/AAAAAAAAFUI/0amFNqCfPJ8w-WyftTbcI2NSv-HsvM-rACLcBGAs/s1600/jpa-states.png)

### 02-1. 엔티티 생명주기

1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계없는 상태
2. 영속(managed): 영속성 컨텍스트에 저장된 상태(<- persist/merge/flush/find/JPQL)
3. 준영속(detached) : 영속성 컨텍스트에 저장되었다가 분리된 상태(<- detach, clear, close)
4. 삭제(remove): 삭제된 상태(<-flush)

```java
//(1)비영속 transient/new
Member member=new Member();
member.setId(1);
member.setName("홍길동");
member.setAge(26);

//(2)영속 managed
entityManager.persist(member);

//(3)준영속 detached
entityManager.detach(member);
```
```java
//(4) 삭제 remove
entityManager.remove(member);
```

## 03. 영속성 컨텍스트 특징

1. 영속상태는 식별자 값이 반드시 있어야 함
2. 플러시
- 트랜잭션 커밋하는 순간 JPA는 영속성 컨텍스트에 새로 저장된 엔티티를 db에 반영하는 것!!

3. 영속성 컨텍스트의 엔티티 관리시 장점

(1) 1차 캐시

- 1차 캐시에 메모리에 있는 데이터가 존재하는지 먼저 확인하고, 없으면 데이터베이스에서 조회

(2) 동일성 보장

- 동일성: 주소값까지 같은 것
- 동등성: 값이 같은 것

(3) 트랜잭션을 지원하는 `쓰기 지연`(transactional write-behind)

- 트랜잭션 커밋하기 전까지 쿼리를 모아두는 것!

(4) 변경 감지(dirty checking)

- 기존 SQL 쿼리에서는 상황에 따라 쿼리가 많아질 수도 있고, 비즈니스 로직 분석을 위해 SQL을 계속 확인해야 하는 `직간접적으로 SQL에 의존적인 상황을 조성` 한다는 문제점이 있다!
- 그런데 JPA는 변경사항을 감지해서 왠지!!!
```java
member.setAge(27);
```
위와 같이 특정 필드값을 변경해준 후
```java
entityManager.update(member);
```
엔티티 매니저를 활용해서 엔티티 변경을 갱신/등록해줘야할 것 같지만, 
```java
member.setAge(27);
```
과 같이 값/로직만 변경해줘도 변경된 사항이 적용된다!

<변경 감지시, 발생되는 과정>

![변경감지 Dirty Checking-김영한님 자바ORM표준 JPA 프로그래밍 책으로 공부하는 jpa, QueryDSL](https://media.vlpt.us/post-images/conatuseus/b5d57200-d0a0-11e9-90a8-3bdc8e61daef/image.png)

> 사진 출처: 김영한님 자바ORM표준 JPA 프로그래밍 책

1. 트랜잭션 커밋
2. 엔티티 매니저 내부에서 플러시(flush()) 호출
3. 엔티티와 스냅샷을 비교해서 변경된 엔티티 찾기
4. 변경된 엔티티가 있을 시, 수정쿼리 생성하여 쓰기 지연 SQL 저장소에 보관
5. 쓰기 지연 저장소의 SQL을 DB에 보내기
6. DB 트랜잭션 커밋하기

✨ 변경감지는 `영속성 컨텍스트가 관리하는 영속 상태의 엔티티에만` 적용됨!

- 컬럼이 30개 이상이 되면 `@DynamicUpdate`를 활용해서 동적 수정 쿼리를 구성하는 것이 보다 권장된다고 한다! 마찬가지로 동적 삽입 쿼리는 `@DynamicInsert`도 있다고 한다!

(5) 지연 로딩

### 03-1. 플러시(flush) [비영속에서 영속으로!!]

개념 : `영속성 컨텍스트의 변경내용을 DB에 반영`

1. 변경감지가 동작하여, 영속성 컨텍스트에 있는 모든 엔티티를 `스냅샷과 비교` ➡ 수정된 엔티티 탐색
▶ 이렇게 되었을 때, 수정된 엔티티에 대한 수정쿼리가 작성되어 `쓰기 지연 SQL 저장소`에 `등록`됨!!

2. 쓰기 지연 SQL 저장소에 있던 쿼리가 `flush()` 실행시, DB에 전송됨(등록, 수정, 삭제 쿼리)

[영속성 컨텍스트를 플러시하는 방법]

1. `entityManager.flush()`를 직접 호출
2. `트랜잭션 커밋` 시 플러시 자동 호출
```java
entityTransaction.commit();
```
-  ✅ JPA 가 없을때는, 트랜잭션 커밋만으로는 DB에 어떤 데이터도 반영되지 않아서 꼭 플러시를 호출해야만 했다. 하지만!! `JPA`는 인해서 `트랜잭션 호출 시 자동으로 플러시가 호출`될 수 있도록 지원하는 간편함을 지원하고 있다!

3. `JPQL 쿼리 실행` / `Criteria 쿼리 실행` 시 플러시 자동 호출
(객체지향 쿼리 실행 시 플러시 자동 호출)
- find() 메서드에서는 작동하지 않는다!!
```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.*;  
import java.util.List;  
  
public class JPQLOverview {  
    public static void main(String[] args) {  
        /**  
 * JPQL과 같은 객체 지향 쿼리를 활용했을 때 flush()가 자동 호출된다!  
 * */  //1.EntityManagerFactory 객체 만들기  
  EntityManagerFactory emf=  
                Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체 만들기  
  EntityManager em=  
                emf.createEntityManager();  
  //3.transaction  
  EntityTransaction entityTransaction=  
                em.getTransaction();  
  
 try{  
            entityTransaction.begin();  
  
  //4. memberA,memberB, memberC 세개 객체를 만들어서 영속성 컨텍스트에 등록하기  
  Member memberA=new Member();  
  Member memberB=new Member();  
  Member memberC=new Member();  
  
  memberA.setId(2);  
  memberA.setAge(20);  
  memberA.setName("가길동");  
  em.persist(memberA);  
  
  memberB.setId(3);  
  memberB.setAge(21);  
  memberB.setName("나길동");  
  em.persist(memberB);  
  
  memberC.setId(4);  
  memberC.setAge(22);  
  memberC.setName("다길동");  
  em.persist(memberC);  
  
  
  System.out.println("삽입할 객체들: "+memberA);  
  System.out.println("삽입할 객체들: "+memberB);  
  System.out.println("삽입할 객체들: "+memberC);  
  //5. JPQL 실행  
  //중간이 쿼리 실행해서 자동으로 플러시 호출  
  TypedQuery query=em.createQuery("select m from Member m",Member.class);  
  List<Member> members=query.getResultList();  
  
  members.forEach(i->{  
                System.out.println("member: "+i);  
  });  
  
  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            em.close();  
  emf.close();  
  }  
  
    }  
}
```

위와 같이 먼저 JPQL을 실행해주게 되면, 자동으로 플러시가 호출되여 쓰기지연SQL에 저장되었던 쿼리가 플러시처리되어 DB로 쿼리가 적용/이동되어 실행된다!

```
10:05:10.482 [main] DEBUG org.hibernate.SQL - 
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
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result set row: 0
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#1]
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result set row: 1
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#2]
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result set row: 2
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#3]
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result set row: 3
10:05:10.482 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#4]
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Resolving attributes for [com.example.ch02jpastart1.jpabook.start.Member#1]
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Processing attribute `age` : value = 12
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Attribute (`age`)  - enhanced for lazy-loading? - false
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Processing attribute `name` : value = 홍길동
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Attribute (`name`)  - enhanced for lazy-loading? - false
10:05:10.482 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Done materializing entity [com.example.ch02jpastart1.jpabook.start.Member#1]
member: Member(id=1, name=홍길동, age=12)
member: Member(id=2, name=가길동, age=20)
member: Member(id=3, name=나길동, age=21)
member: Member(id=4, name=다길동, age=22)
```

#### 03-1-1.  플러시 모드 옵션

`javax.persistence.FlushModeType`을 활용!!

- FlushModeType.AUTO: 커밋이나 쿼리를 실행할 때 플러시(기본값)

- FlushModeType.COMMIT: 커밋할 때만 플러시(`성능 최적화`를 생각할 때 고려될 수 있다!)

`플러시 모드는 어떻게 설정할까??`

▶
```java
entityManager.setFlushMode(FlushModeType.AUTO);
entityManager.setFlushMode(FlushModeType.COMMIT);
```

### 03-2. 영속에서 준영속으로!

`영속`: 영속성 컨텍스트에서 엔티티를 관리하는 것

`준영속`: 영속성 컨텍스트에서 엔티티를 분리 detached 하는 것!!

↔ 영속성 컨텍스트가 제공하는 기능을 사용할 수 없음!!
(변경감지, 트랜잭션 커밋 시 자동 플러시, JPQL이나 Criteria 쿼리 실행 시 자동 플러시 호출, 플러시 호출, 쓰기 지연 SQL 저장소에서의 쿼리가 제거되어서 DB에 저장되지도 않음!!)

#### 03-2-1. 영속상태의 엔티티를 준영속상태로 변경하는 방법

1. entityManager.detach(entity)

- 특정 엔티티만 영속성 컨텍스트에서 분리 detach 하여 준영속 상태로 만드는 것
```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
  
public class DetachTest {  
    public static void main(String[] args) {  
        //1.EntityManagerFactory 객체  
  EntityManagerFactory emf=  
                Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체  
  EntityManager em=  
                emf.createEntityManager();  
  
  //3.EntityTransaction객체  
  EntityTransaction tx=  
                em.getTransaction();  
  
 try {  
            //4.트랜잭션 시작  
  tx.begin();  
  //5.logic(영속상태에서 비영속상태로 만들기)  
  logic(em);  
  //6.트랜잭션 커밋  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            em.close();  
  }  
        emf.close();  
  }  
  
    public static void logic(EntityManager entityManager){  
        //01.영속성 컨텍스트로 관리하기(managed state)  
  Member member1=new Member();  
  member1.setId(2);  
  member1.setAge(24);  
  member1.setName("abc");  
  entityManager.persist(member1);  
  
  //02.detach 상태로 만들기  
  //방법1.detach 메서드 활용  
  entityManager.detach(member1);  
  }  
}
```
- 위의 코드를 과정별로 끊어서 살펴보도록 하자

(1) `persist`를 통해서 영속성 컨텍스트로 등록하고

(2) 1차캐시의 엔티티로 저장됨과 동시에, 쓰기지연SQL에 쿼리가 저장된다

(3)  detach가 호출되는 순간, 1차 캐시부터 쓰기 지연 SQL 저장소까지 해당 엔티티를 관리하기 위한 모든 정보가 제거됨

2. entityManager.clear()

- 영속성 컨텍스트 완전 초기화

```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class DetachTestByClear {  
    /**  
 * @author JISOOJEONG  
 * entityManager.clear를 활용해서 managed state를 clear state로 변경하기  
  * */  
  public static void main(String[] args) {  
        //1.EntityManagerFactory 객체  
  EntityManagerFactory emf=  
                Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체  
  EntityManager em=  
                emf.createEntityManager();  
  //3.EntityTransaction 객체  
  EntityTransaction tx=  
                em.getTransaction();  
  
 try{  
            //4.트랜잭션 시작  
  tx.begin();  
  
  //5.find 로직 시작  
  logic(em);  
  //6.트랜잭션 커밋  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            em.close();  
  }  
        emf.close();  
  }  
  
    public static void logic(EntityManager entityManager){  
        //1.영속성 컨텍스트로 등록  
  Member member=new Member();  
  member.setId(2);  
  member.setAge(26);  
  member.setName("가나다");  
  
  entityManager.persist(member);  
  
  //2.find로 등록된 객체 조회하기  
  Member findMember=entityManager.find(Member.class,2);  
  System.out.println("찾은 객체: "+findMember);  
  
  //3.영속성 컨텍스트 초기화  
  entityManager.clear();  
  //4.모든 객체를 확인해보자  
  List<Member> members=  
                entityManager.createQuery("select m from Member m",Member.class)  
                        .getResultList();  
  
  System.out.println("지금 저장된 모든 데이터를 조회="+members);  
  }  
}
```
간단하게 영속성 컨텍스트로 엔티티를 등록한 후에, detach 상태로 만드는 방법 중 또다른 하나인 `entityManager.clear()`를 진행하고,

그 후, 과연 등록된 엔티티가 그대로 있는지 확인하는 작업을 진행해보았다!

그 결과, 아래와 같이, 영속성 컨텍스트가 완전 초기화된 후에는 등록된 엔티티가 조회되지 않는 모습을 확인해볼 수 있다

(마찬가지로, 수정작업을 한다 하더라도, 변경감지가 되지 않을 것이다!)
```
C:\storm\jdk\jdk11\bin\java.exe "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.1\lib\idea_rt.jar=56236:C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.1\bin" -Dfile.encoding=UTF-8 -classpath D:\Jpa\ch02-jpa-start1\target\classes;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\2.6.2\spring-boot-starter-data-jpa-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-aop\2.6.2\spring-boot-starter-aop-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-aop\5.3.14\spring-aop-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\2.6.2\spring-boot-starter-jdbc-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\com\zaxxer\HikariCP\4.0.3\HikariCP-4.0.3.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-jdbc\5.3.14\spring-jdbc-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\transaction\jakarta.transaction-api\1.3.3\jakarta.transaction-api-1.3.3.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\persistence\jakarta.persistence-api\2.2.3\jakarta.persistence-api-2.2.3.jar;C:\Users\JISOOJEONG\.m2\repository\org\hibernate\hibernate-core\5.6.3.Final\hibernate-core-5.6.3.Final.jar;C:\Users\JISOOJEONG\.m2\repository\org\jboss\logging\jboss-logging\3.4.2.Final\jboss-logging-3.4.2.Final.jar;C:\Users\JISOOJEONG\.m2\repository\net\bytebuddy\byte-buddy\1.11.22\byte-buddy-1.11.22.jar;C:\Users\JISOOJEONG\.m2\repository\antlr\antlr\2.7.7\antlr-2.7.7.jar;C:\Users\JISOOJEONG\.m2\repository\org\jboss\jandex\2.2.3.Final\jandex-2.2.3.Final.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\classmate\1.5.1\classmate-1.5.1.jar;C:\Users\JISOOJEONG\.m2\repository\org\hibernate\common\hibernate-commons-annotations\5.1.2.Final\hibernate-commons-annotations-5.1.2.Final.jar;C:\Users\JISOOJEONG\.m2\repository\org\glassfish\jaxb\jaxb-runtime\2.3.5\jaxb-runtime-2.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\org\glassfish\jaxb\txw2\2.3.5\txw2-2.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\com\sun\istack\istack-commons-runtime\3.0.12\istack-commons-runtime-3.0.12.jar;C:\Users\JISOOJEONG\.m2\repository\com\sun\activation\jakarta.activation\1.2.2\jakarta.activation-1.2.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\data\spring-data-jpa\2.6.0\spring-data-jpa-2.6.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\data\spring-data-commons\2.6.0\spring-data-commons-2.6.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-orm\5.3.14\spring-orm-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-context\5.3.14\spring-context-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-tx\5.3.14\spring-tx-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-beans\5.3.14\spring-beans-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\slf4j\slf4j-api\1.7.32\slf4j-api-1.7.32.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-aspects\5.3.14\spring-aspects-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.6.2\spring-boot-starter-web-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter\2.6.2\spring-boot-starter-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot\2.6.2\spring-boot-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\2.6.2\spring-boot-autoconfigure-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-logging\2.6.2\spring-boot-starter-logging-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\ch\qos\logback\logback-classic\1.2.9\logback-classic-1.2.9.jar;C:\Users\JISOOJEONG\.m2\repository\ch\qos\logback\logback-core\1.2.9\logback-core-1.2.9.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.17.0\log4j-to-slf4j-2.17.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\logging\log4j\log4j-api\2.17.0\log4j-api-2.17.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\slf4j\jul-to-slf4j\1.7.32\jul-to-slf4j-1.7.32.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\annotation\jakarta.annotation-api\1.3.5\jakarta.annotation-api-1.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\org\yaml\snakeyaml\1.29\snakeyaml-1.29.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-json\2.6.2\spring-boot-starter-json-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.13.1\jackson-databind-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.13.1\jackson-annotations-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.13.1\jackson-core-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.13.1\jackson-datatype-jdk8-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.13.1\jackson-datatype-jsr310-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.13.1\jackson-module-parameter-names-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-tomcat\2.6.2\spring-boot-starter-tomcat-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\9.0.56\tomcat-embed-core-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\9.0.56\tomcat-embed-el-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\9.0.56\tomcat-embed-websocket-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-web\5.3.14\spring-web-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-webmvc\5.3.14\spring-webmvc-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-expression\5.3.14\spring-expression-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\com\h2database\h2\1.4.200\h2-1.4.200.jar;C:\Users\JISOOJEONG\.m2\repository\org\projectlombok\lombok\1.18.22\lombok-1.18.22.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\2.3.3\jakarta.xml.bind-api-2.3.3.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\activation\jakarta.activation-api\1.2.2\jakarta.activation-api-1.2.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-core\5.3.14\spring-core-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-jcl\5.3.14\spring-jcl-5.3.14.jar com.example.ch02jpastart1.jpabook.start.DetachTestByClear
10:42:03.274 [main] DEBUG org.jboss.logging - Logging Provider: org.jboss.logging.Log4j2LoggerProvider
10:42:03.407 [main] DEBUG org.hibernate.jpa.HibernatePersistenceProvider - Located and parsed 1 persistence units; checking each
10:42:03.407 [main] DEBUG org.hibernate.jpa.HibernatePersistenceProvider - Checking persistence-unit [name=jpabook, explicit-provider=null] against incoming persistence unit name [jpabook]
10:42:03.407 [main] DEBUG org.hibernate.jpa.boot.spi.ProviderChecker - No PersistenceProvider explicitly requested, assuming Hibernate
10:42:03.423 [main] DEBUG org.hibernate.jpa.internal.util.LogHelper - PersistenceUnitInfo [
	name: jpabook
	persistence provider classname: null
	classloader: null
	excludeUnlistedClasses: false
	JTA datasource: null
	Non JTA datasource: null
	Transaction type: RESOURCE_LOCAL
	PU root URL: file:/D:/Jpa/ch02-jpa-start1/target/classes/
	Shared Cache Mode: null
	Validation Mode: null
	Jar files URLs []
	Managed classes names [
		com.example.ch02jpastart1.jpabook.start.Member]
	Mapping files names []
	Properties [
		javax.persistence.jdbc.driver: org.h2.Driver
		javax.persistence.jdbc.password: 
		hibernate.dialect: org.hibernate.dialect.H2Dialect
		javax.persistence.jdbc.url: jdbc:h2:tcp://localhost/~/test
		hibernate.show_sql: true
		hibernate.format_sql: true
		javax.persistence.jdbc.user: sa]
10:42:03.423 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.cfg.beanvalidation.BeanValidationIntegrator].
10:42:03.423 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.secure.spi.JaccIntegrator].
10:42:03.439 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.cache.internal.CollectionCacheInvalidator].
10:42:03.475 [main] INFO org.hibernate.Version - HHH000412: Hibernate ORM core version 5.6.3.Final
10:42:03.475 [main] DEBUG org.hibernate.cfg.Environment - HHH000206: hibernate.properties not found
10:42:03.638 [main] DEBUG org.hibernate.service.spi.ServiceBinding - Overriding existing service binding [org.hibernate.secure.spi.JaccService]
10:42:03.656 [main] DEBUG org.hibernate.cache.internal.RegionFactoryInitiator - Cannot default RegionFactory based on registered strategies as `[]` RegionFactory strategies were registered
10:42:03.656 [main] DEBUG org.hibernate.cache.internal.RegionFactoryInitiator - Cache region factory : org.hibernate.cache.internal.NoCachingRegionFactory
10:42:03.660 [main] INFO org.hibernate.annotations.common.Version - HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration boolean -> org.hibernate.type.BooleanType@66fdec9
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration boolean -> org.hibernate.type.BooleanType@66fdec9
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Boolean -> org.hibernate.type.BooleanType@66fdec9
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration numeric_boolean -> org.hibernate.type.NumericBooleanType@52851b44
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration true_false -> org.hibernate.type.TrueFalseType@2dca0d64
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration yes_no -> org.hibernate.type.YesNoType@5ef6ae06
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte -> org.hibernate.type.ByteType@1d3ac898
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte -> org.hibernate.type.ByteType@1d3ac898
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Byte -> org.hibernate.type.ByteType@1d3ac898
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration character -> org.hibernate.type.CharacterType@6ad59d92
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration char -> org.hibernate.type.CharacterType@6ad59d92
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Character -> org.hibernate.type.CharacterType@6ad59d92
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration short -> org.hibernate.type.ShortType@63dd899
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration short -> org.hibernate.type.ShortType@63dd899
10:42:04.092 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Short -> org.hibernate.type.ShortType@63dd899
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration integer -> org.hibernate.type.IntegerType@34cdeda2
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration int -> org.hibernate.type.IntegerType@34cdeda2
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Integer -> org.hibernate.type.IntegerType@34cdeda2
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration long -> org.hibernate.type.LongType@4b41e4dd
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration long -> org.hibernate.type.LongType@4b41e4dd
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Long -> org.hibernate.type.LongType@4b41e4dd
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration float -> org.hibernate.type.FloatType@66746f57
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration float -> org.hibernate.type.FloatType@66746f57
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Float -> org.hibernate.type.FloatType@66746f57
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration double -> org.hibernate.type.DoubleType@767e20cf
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration double -> org.hibernate.type.DoubleType@767e20cf
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Double -> org.hibernate.type.DoubleType@767e20cf
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration big_decimal -> org.hibernate.type.BigDecimalType@52066604
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.math.BigDecimal -> org.hibernate.type.BigDecimalType@52066604
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration big_integer -> org.hibernate.type.BigIntegerType@189aa67a
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.math.BigInteger -> org.hibernate.type.BigIntegerType@189aa67a
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration string -> org.hibernate.type.StringType@31d0e481
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.String -> org.hibernate.type.StringType@31d0e481
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration nstring -> org.hibernate.type.StringNVarcharType@626c44e7
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ncharacter -> org.hibernate.type.CharacterNCharType@3b00856b
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration url -> org.hibernate.type.UrlType@1338fb5
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.net.URL -> org.hibernate.type.UrlType@1338fb5
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Duration -> org.hibernate.type.DurationType@41dd05a
10:42:04.107 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.Duration -> org.hibernate.type.DurationType@41dd05a
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Instant -> org.hibernate.type.InstantType@4aa83f4f
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.Instant -> org.hibernate.type.InstantType@4aa83f4f
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalDateTime -> org.hibernate.type.LocalDateTimeType@ebb6851
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalDateTime -> org.hibernate.type.LocalDateTimeType@ebb6851
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalDate -> org.hibernate.type.LocalDateType@3a6f2de3
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalDate -> org.hibernate.type.LocalDateType@3a6f2de3
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration LocalTime -> org.hibernate.type.LocalTimeType@34f7234e
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.LocalTime -> org.hibernate.type.LocalTimeType@34f7234e
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration OffsetDateTime -> org.hibernate.type.OffsetDateTimeType@1d2bd371
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.OffsetDateTime -> org.hibernate.type.OffsetDateTimeType@1d2bd371
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration OffsetTime -> org.hibernate.type.OffsetTimeType@68ead359
10:42:04.123 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.OffsetTime -> org.hibernate.type.OffsetTimeType@68ead359
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ZonedDateTime -> org.hibernate.type.ZonedDateTimeType@7e7b159b
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.time.ZonedDateTime -> org.hibernate.type.ZonedDateTimeType@7e7b159b
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration date -> org.hibernate.type.DateType@7966baa7
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Date -> org.hibernate.type.DateType@7966baa7
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration time -> org.hibernate.type.TimeType@302a07d
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Time -> org.hibernate.type.TimeType@302a07d
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration timestamp -> org.hibernate.type.TimestampType@708400f6
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Timestamp -> org.hibernate.type.TimestampType@708400f6
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Date -> org.hibernate.type.TimestampType@708400f6
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration dbtimestamp -> org.hibernate.type.DbTimestampType@1b2c4efb
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar -> org.hibernate.type.CalendarType@6f2cfcc2
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Calendar -> org.hibernate.type.CalendarType@6f2cfcc2
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.GregorianCalendar -> org.hibernate.type.CalendarType@6f2cfcc2
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar_date -> org.hibernate.type.CalendarDateType@ec2cc4
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration calendar_time -> org.hibernate.type.CalendarTimeType@710b18a6
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration locale -> org.hibernate.type.LocaleType@55562aa9
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Locale -> org.hibernate.type.LocaleType@55562aa9
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration currency -> org.hibernate.type.CurrencyType@3ecd267f
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.Currency -> org.hibernate.type.CurrencyType@3ecd267f
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration timezone -> org.hibernate.type.TimeZoneType@4c5474f5
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.TimeZone -> org.hibernate.type.TimeZoneType@4c5474f5
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration class -> org.hibernate.type.ClassType@3569fc08
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Class -> org.hibernate.type.ClassType@3569fc08
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration uuid-binary -> org.hibernate.type.UUIDBinaryType@522a32b1
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.util.UUID -> org.hibernate.type.UUIDBinaryType@522a32b1
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration uuid-char -> org.hibernate.type.UUIDCharType@5ee2b6f9
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration binary -> org.hibernate.type.BinaryType@dfddc9a
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration byte[] -> org.hibernate.type.BinaryType@dfddc9a
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [B -> org.hibernate.type.BinaryType@dfddc9a
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration wrapper-binary -> org.hibernate.type.WrapperBinaryType@5f7b97da
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Byte[] -> org.hibernate.type.WrapperBinaryType@5f7b97da
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [Ljava.lang.Byte; -> org.hibernate.type.WrapperBinaryType@5f7b97da
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration row_version -> org.hibernate.type.RowVersionType@52d239ba
10:42:04.138 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration image -> org.hibernate.type.ImageType@2f05be7f
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration characters -> org.hibernate.type.CharArrayType@52de51b6
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration char[] -> org.hibernate.type.CharArrayType@52de51b6
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [C -> org.hibernate.type.CharArrayType@52de51b6
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration wrapper-characters -> org.hibernate.type.CharacterArrayType@2e9fda69
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration [Ljava.lang.Character; -> org.hibernate.type.CharacterArrayType@2e9fda69
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration Character[] -> org.hibernate.type.CharacterArrayType@2e9fda69
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration text -> org.hibernate.type.TextType@2371aaca
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration ntext -> org.hibernate.type.NTextType@5553d0f5
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration blob -> org.hibernate.type.BlobType@424fd310
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Blob -> org.hibernate.type.BlobType@424fd310
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_blob -> org.hibernate.type.MaterializedBlobType@6e6d5d29
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration clob -> org.hibernate.type.ClobType@4e858e0a
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.Clob -> org.hibernate.type.ClobType@4e858e0a
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration nclob -> org.hibernate.type.NClobType@19553973
10:42:04.154 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.sql.NClob -> org.hibernate.type.NClobType@19553973
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_clob -> org.hibernate.type.MaterializedClobType@7ce97ee5
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration materialized_nclob -> org.hibernate.type.MaterializedNClobType@32811494
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration serializable -> org.hibernate.type.SerializableType@3f07b12c
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration object -> org.hibernate.type.ObjectType@31e4bb20
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration java.lang.Object -> org.hibernate.type.ObjectType@31e4bb20
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_date -> org.hibernate.type.AdaptedImmutableType@62f68dff
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_time -> org.hibernate.type.AdaptedImmutableType@f001896
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_timestamp -> org.hibernate.type.AdaptedImmutableType@13f17eb4
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_dbtimestamp -> org.hibernate.type.AdaptedImmutableType@1d0d6318
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_calendar -> org.hibernate.type.AdaptedImmutableType@4bc28c33
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_calendar_date -> org.hibernate.type.AdaptedImmutableType@4409e975
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_binary -> org.hibernate.type.AdaptedImmutableType@5c153b9e
10:42:04.161 [main] DEBUG org.hibernate.type.BasicTypeRegistry - Adding type registration imm_serializable -> org.hibernate.type.AdaptedImmutableType@2a7686a7
10:42:04.161 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting ScanEnvironment [org.hibernate.jpa.boot.internal.StandardJpaScanEnvironmentImpl@5bc9ba1d] into BootstrapContext; was [null]
10:42:04.161 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting ScanOptions [org.hibernate.boot.archive.scan.internal.StandardScanOptions@1021f6c9] into BootstrapContext; was [org.hibernate.boot.archive.scan.internal.StandardScanOptions@7516e4e5]
10:42:04.207 [main] DEBUG org.hibernate.boot.internal.BootstrapContextImpl - Injecting JPA temp ClassLoader [null] into BootstrapContext; was [null]
10:42:04.207 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - ClassLoaderAccessImpl#injectTempClassLoader(null) [was null]
10:42:04.207 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid2] -> [org.hibernate.id.UUIDGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [guid] -> [org.hibernate.id.GUIDGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid] -> [org.hibernate.id.UUIDHexGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [uuid.hex] -> [org.hibernate.id.UUIDHexGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [assigned] -> [org.hibernate.id.Assigned]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [identity] -> [org.hibernate.id.IdentityGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [select] -> [org.hibernate.id.SelectGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [sequence] -> [org.hibernate.id.enhanced.SequenceStyleGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [seqhilo] -> [org.hibernate.id.SequenceHiLoGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [increment] -> [org.hibernate.id.IncrementGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [foreign] -> [org.hibernate.id.ForeignGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [sequence-identity] -> [org.hibernate.id.SequenceIdentityGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [enhanced-sequence] -> [org.hibernate.id.enhanced.SequenceStyleGenerator]
10:42:04.223 [main] DEBUG org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory - Registering IdentifierGenerator strategy [enhanced-table] -> [org.hibernate.id.enhanced.TableGenerator]
10:42:04.223 [main] WARN org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
10:42:04.223 [main] INFO org.hibernate.orm.connections.pooling - HHH10001005: using driver [org.h2.Driver] at URL [jdbc:h2:tcp://localhost/~/test]
10:42:04.223 [main] INFO org.hibernate.orm.connections.pooling - HHH10001001: Connection properties: {password=, user=sa}
10:42:04.223 [main] INFO org.hibernate.orm.connections.pooling - HHH10001003: Autocommit mode: false
10:42:04.239 [main] DEBUG org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl - Initializing Connection pool with 1 Connections
10:42:04.239 [main] INFO org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl - HHH000115: Hibernate connection pool size: 20 (min=1)
10:42:04.292 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - Database ->
       name : H2
    version : 1.4.200 (2019-10-14)
      major : 1
      minor : 4
10:42:04.292 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - Driver ->
       name : H2 JDBC Driver
    version : 1.4.200 (2019-10-14)
      major : 1
      minor : 4
10:42:04.292 [main] DEBUG org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator - JDBC version : 4.1
10:42:04.292 [main] INFO org.hibernate.dialect.Dialect - HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
10:42:04.339 [main] DEBUG org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder - JDBC driver metadata reported database stores quoted identifiers in neither upper, lower nor mixed case
10:42:04.354 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration@15bcf458] to MetadataBuildingContext [org.hibernate.boot.internal.MetadataBuildingContextRootImpl@5af9926a]
10:42:04.377 [main] DEBUG org.hibernate.boot.model.relational.Namespace - Created database namespace [logicalName=Name{catalog=null, schema=null}, physicalName=Name{catalog=null, schema=null}]
10:42:04.392 [main] DEBUG org.hibernate.cfg.AnnotationBinder - Binding entity from annotated class: com.example.ch02jpastart1.jpabook.start.Member
10:42:04.408 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3DiscriminatorColumn{logicalColumnName'DTYPE', discriminatorTypeName='string'}
10:42:04.408 [main] DEBUG org.hibernate.cfg.annotations.EntityBinder - Import with entity name Member
10:42:04.408 [main] DEBUG org.hibernate.cfg.annotations.EntityBinder - Bind entity com.example.ch02jpastart1.jpabook.start.Member on table member
10:42:04.423 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=ID, insertable=true, updatable=true, unique=false}
10:42:04.423 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - Not known whether passed class name [com.example.ch02jpastart1.jpabook.start.Member] is safe
10:42:04.423 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - No temp ClassLoader provided; using live ClassLoader for loading potentially unsafe class : com.example.ch02jpastart1.jpabook.start.Member
10:42:04.423 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property id with lazy=false
10:42:04.423 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:id]
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for id
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property id
10:42:04.439 [main] DEBUG org.hibernate.cfg.BinderHelper - #makeIdGenerator(org.hibernate.mapping.SimpleValue([org.hibernate.mapping.Column(ID)]), id, assigned, , ...)
10:42:04.439 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=AGE, insertable=true, updatable=true, unique=false}
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property age with lazy=false
10:42:04.439 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:age]
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for age
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property age
10:42:04.439 [main] DEBUG org.hibernate.cfg.Ejb3Column - Binding column: Ejb3Column{table=org.hibernate.mapping.Table(member), mappingColumn=NAME, insertable=true, updatable=true, unique=false}
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - MetadataSourceProcessor property name with lazy=false
10:42:04.439 [main] DEBUG org.hibernate.cfg.AbstractPropertyHolder - Attempting to locate auto-apply AttributeConverter for property [com.example.ch02jpastart1.jpabook.start.Member:name]
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - building SimpleValue for name
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.PropertyBinder - Building property name
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for id
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for age
10:42:04.439 [main] DEBUG org.hibernate.cfg.annotations.SimpleValueBinder - Starting fillSimpleValue for name
10:42:04.439 [main] DEBUG org.hibernate.mapping.PrimaryKey - Forcing column [id] to be non-null as it is part of the primary key for table [member]
10:42:04.477 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Building session factory
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - SessionFactory name : null
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Automatic flush during beforeCompletion(): enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Automatic session close at end of transaction: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Statistics: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Deleted entity synthetic identifier rollback: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Default entity-mode: pojo
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Check Nullability in Core (should be disabled when Bean Validation is on): enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Allow initialization of lazy state outside session : disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Using BatchFetchStyle : LEGACY
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Default batch fetch size: -1
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Maximum outer join fetch depth: null
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Default null ordering: NONE
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Order SQL updates by primary key: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Order SQL inserts for batching: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - multi-tenancy strategy : NONE
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JTA Track by Thread: enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Query language substitutions: {}
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Named query checking : enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Second-level query cache: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Second-level query cache factory: null
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache region prefix: null
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Optimize second-level cache for minimal puts: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Structured second-level cache entries: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Second-level cache direct-reference entries: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Automatic eviction of collection cache: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JDBC batch size: 15
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JDBC batch updates for versioned data: enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Scrollable result sets: enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Wrap result sets: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JDBC3 getGeneratedKeys(): enabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JDBC result set fetch size: null
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Connection release mode: AFTER_TRANSACTION
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - Generate SQL with comments: disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - query : disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - closed-handling : disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - lists : disabled
10:42:04.477 [main] DEBUG org.hibernate.cfg.Settings - JPA compliance - transactions : disabled
10:42:04.524 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
10:42:04.524 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
10:42:04.524 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Session factory constructed with filter configurations : {}
10:42:04.524 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Instantiating session factory with properties: {sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, hibernate.format_sql=true, java.specification.version=11, sun.cpu.isalist=amd64, sun.jnu.encoding=MS949, hibernate.dialect=org.hibernate.dialect.H2Dialect, java.class.path=D:\Jpa\ch02-jpa-start1\target\classes;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\2.6.2\spring-boot-starter-data-jpa-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-aop\2.6.2\spring-boot-starter-aop-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-aop\5.3.14\spring-aop-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\2.6.2\spring-boot-starter-jdbc-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\com\zaxxer\HikariCP\4.0.3\HikariCP-4.0.3.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-jdbc\5.3.14\spring-jdbc-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\transaction\jakarta.transaction-api\1.3.3\jakarta.transaction-api-1.3.3.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\persistence\jakarta.persistence-api\2.2.3\jakarta.persistence-api-2.2.3.jar;C:\Users\JISOOJEONG\.m2\repository\org\hibernate\hibernate-core\5.6.3.Final\hibernate-core-5.6.3.Final.jar;C:\Users\JISOOJEONG\.m2\repository\org\jboss\logging\jboss-logging\3.4.2.Final\jboss-logging-3.4.2.Final.jar;C:\Users\JISOOJEONG\.m2\repository\net\bytebuddy\byte-buddy\1.11.22\byte-buddy-1.11.22.jar;C:\Users\JISOOJEONG\.m2\repository\antlr\antlr\2.7.7\antlr-2.7.7.jar;C:\Users\JISOOJEONG\.m2\repository\org\jboss\jandex\2.2.3.Final\jandex-2.2.3.Final.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\classmate\1.5.1\classmate-1.5.1.jar;C:\Users\JISOOJEONG\.m2\repository\org\hibernate\common\hibernate-commons-annotations\5.1.2.Final\hibernate-commons-annotations-5.1.2.Final.jar;C:\Users\JISOOJEONG\.m2\repository\org\glassfish\jaxb\jaxb-runtime\2.3.5\jaxb-runtime-2.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\org\glassfish\jaxb\txw2\2.3.5\txw2-2.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\com\sun\istack\istack-commons-runtime\3.0.12\istack-commons-runtime-3.0.12.jar;C:\Users\JISOOJEONG\.m2\repository\com\sun\activation\jakarta.activation\1.2.2\jakarta.activation-1.2.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\data\spring-data-jpa\2.6.0\spring-data-jpa-2.6.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\data\spring-data-commons\2.6.0\spring-data-commons-2.6.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-orm\5.3.14\spring-orm-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-context\5.3.14\spring-context-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-tx\5.3.14\spring-tx-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-beans\5.3.14\spring-beans-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\slf4j\slf4j-api\1.7.32\slf4j-api-1.7.32.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-aspects\5.3.14\spring-aspects-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.6.2\spring-boot-starter-web-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter\2.6.2\spring-boot-starter-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot\2.6.2\spring-boot-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\2.6.2\spring-boot-autoconfigure-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-logging\2.6.2\spring-boot-starter-logging-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\ch\qos\logback\logback-classic\1.2.9\logback-classic-1.2.9.jar;C:\Users\JISOOJEONG\.m2\repository\ch\qos\logback\logback-core\1.2.9\logback-core-1.2.9.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.17.0\log4j-to-slf4j-2.17.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\logging\log4j\log4j-api\2.17.0\log4j-api-2.17.0.jar;C:\Users\JISOOJEONG\.m2\repository\org\slf4j\jul-to-slf4j\1.7.32\jul-to-slf4j-1.7.32.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\annotation\jakarta.annotation-api\1.3.5\jakarta.annotation-api-1.3.5.jar;C:\Users\JISOOJEONG\.m2\repository\org\yaml\snakeyaml\1.29\snakeyaml-1.29.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-json\2.6.2\spring-boot-starter-json-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.13.1\jackson-databind-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.13.1\jackson-annotations-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.13.1\jackson-core-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.13.1\jackson-datatype-jdk8-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.13.1\jackson-datatype-jsr310-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.13.1\jackson-module-parameter-names-2.13.1.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\boot\spring-boot-starter-tomcat\2.6.2\spring-boot-starter-tomcat-2.6.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\9.0.56\tomcat-embed-core-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\9.0.56\tomcat-embed-el-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\9.0.56\tomcat-embed-websocket-9.0.56.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-web\5.3.14\spring-web-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-webmvc\5.3.14\spring-webmvc-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-expression\5.3.14\spring-expression-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\com\h2database\h2\1.4.200\h2-1.4.200.jar;C:\Users\JISOOJEONG\.m2\repository\org\projectlombok\lombok\1.18.22\lombok-1.18.22.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\2.3.3\jakarta.xml.bind-api-2.3.3.jar;C:\Users\JISOOJEONG\.m2\repository\jakarta\activation\jakarta.activation-api\1.2.2\jakarta.activation-api-1.2.2.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-core\5.3.14\spring-core-5.3.14.jar;C:\Users\JISOOJEONG\.m2\repository\org\springframework\spring-jcl\5.3.14\spring-jcl-5.3.14.jar, java.vm.vendor=Eclipse Adoptium, sun.arch.data.model=64, user.variant=, java.vendor.url=https://adoptium.net/, user.timezone=GMT+09:00, javax.persistence.jdbc.user=****, javax.persistence.jdbc.url=jdbc:h2:tcp://localhost/~/test, os.name=Windows 10, java.vm.specification.version=11, jakarta.persistence.jdbc.password=****, sun.java.launcher=SUN_STANDARD, user.country=KR, local.setting.IS_JTA_TXN_COORD=false, sun.boot.library.path=C:\storm\jdk\jdk11\bin, sun.java.command=com.example.ch02jpastart1.jpabook.start.DetachTestByClear, jdk.debug=release, jakarta.persistence.jdbc.driver=org.h2.Driver, sun.cpu.endian=little, user.home=C:\Users\JISOOJEONG, user.language=ko, java.specification.vendor=Oracle Corporation, java.version.date=2021-10-19, java.home=C:\storm\jdk\jdk11, file.separator=\, java.vm.compressedOopsMode=Zero based, jakarta.persistence.jdbc.user=****, line.separator=
, hibernate.persistenceUnitName=jpabook, java.specification.name=Java Platform API Specification, java.vm.specification.vendor=Oracle Corporation, hibernate.transaction.coordinator_class=class org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, javax.persistence.jdbc.driver=org.h2.Driver, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=11.0.13+8, user.name=JISOOJEONG, path.separator=;, hibernate.connection.username=****, os.version=10.0, java.runtime.name=OpenJDK Runtime Environment, hibernate.connection.url=jdbc:h2:tcp://localhost/~/test, file.encoding=UTF-8, hibernate.ejb.persistenceUnitName=jpabook, java.vm.name=OpenJDK 64-Bit Server VM, hibernate.show_sql=true, java.vendor.version=Temurin-11.0.13+8, hibernate.connection.driver_class=org.h2.Driver, java.vendor.url.bug=https://github.com/adoptium/adoptium-support/issues, java.io.tmpdir=C:\Users\JISOOJ~1\AppData\Local\Temp\, java.version=11.0.13, user.dir=D:\Jpa\ch02-jpa-start1, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, hibernate.connection.password=****, sun.os.patch.level=, jakarta.persistence.jdbc.url=jdbc:h2:tcp://localhost/~/test, hibernate.boot.CfgXmlAccessService.key=org.hibernate.boot.registry.StandardServiceRegistryBuilder$1@aa004a0, java.library.path=C:\storm\jdk\jdk11\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\storm\jdk\jdk11\bin;C:\windows\system32;C:\windows;C:\windows\System32\Wbem;C:\windows\System32\WindowsPowerShell\v1.0\;C:\windows\System32\OpenSSH\;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;c:\Program Files (x86)\HP\HP Performance Advisor;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\nodejs\;C:\Program Files\Git\cmd;C:\Users\JISOOJEONG\AppData\Local\Microsoft\WindowsApps;;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.1\bin;;C:\Users\JISOOJEONG\AppData\Roaming\npm;C:\Program Files\Bandizip\;., java.vendor=Eclipse Adoptium, java.vm.info=mixed mode, java.vm.version=11.0.13+8, hibernate.bytecode.use_reflection_optimizer=false, sun.io.unicode.encoding=UnicodeLittle, javax.persistence.jdbc.password=****, java.class.version=55.0}
10:42:04.539 [main] DEBUG org.hibernate.secure.spi.JaccIntegrator - Skipping JACC integration as it was not enabled
10:42:04.539 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - Instantiated session factory
10:42:04.539 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration@15bcf458] to SessionFactoryImpl [org.hibernate.internal.SessionFactoryImpl@7ca0863b]
10:42:04.577 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - Not known whether passed class name [com.example.ch02jpastart1.jpabook.start.Member] is safe
10:42:04.577 [main] DEBUG org.hibernate.boot.internal.ClassLoaderAccessImpl - No temp ClassLoader provided; using live ClassLoader for loading potentially unsafe class : com.example.ch02jpastart1.jpabook.start.Member
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister - Static SQL for entity: com.example.ch02jpastart1.jpabook.start.Member
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Version select: select ID from member where ID =?
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Snapshot select: select member_.ID, member_.AGE as age2_0_, member_.NAME as name3_0_ from member member_ where member_.ID=?
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Insert 0: insert into member (AGE, NAME, ID) values (?, ?, ?)
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Update 0: update member set AGE=?, NAME=? where ID=?
10:42:04.740 [main] DEBUG org.hibernate.persister.entity.AbstractEntityPersister -  Delete 0: delete from member where ID=?
10:42:04.762 [main] DEBUG org.hibernate.loader.plan.build.internal.spaces.QuerySpacesImpl - Adding QuerySpace : uid = <gen:0> -> org.hibernate.loader.plan.build.internal.spaces.EntityQuerySpaceImpl@6a937336]
10:42:04.762 [main] DEBUG org.hibernate.persister.walking.spi.MetamodelGraphWalker - Visiting attribute path : age
10:42:04.762 [main] DEBUG org.hibernate.persister.walking.spi.MetamodelGraphWalker - Visiting attribute path : name
10:42:04.762 [main] DEBUG org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy - Building LoadPlan...
10:42:04.778 [main] DEBUG org.hibernate.loader.plan.exec.internal.LoadQueryJoinAndFetchProcessor - processing queryspace <gen:0>
10:42:04.778 [main] DEBUG org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter - LoadPlan(entity=com.example.ch02jpastart1.jpabook.start.Member)
    - Returns
       - EntityReturnImpl(entity=com.example.ch02jpastart1.jpabook.start.Member, querySpaceUid=<gen:0>, path=com.example.ch02jpastart1.jpabook.start.Member)
    - QuerySpaces
       - EntityQuerySpaceImpl(uid=<gen:0>, entity=com.example.ch02jpastart1.jpabook.start.Member)
          - SQL table alias mapping - member0_
          - alias suffix - 0_
          - suffixed key columns - {id1_0_0_}

10:42:04.778 [main] DEBUG org.hibernate.loader.entity.plan.EntityLoader - Static select for entity com.example.ch02jpastart1.jpabook.start.Member [NONE]: select member0_.ID as id1_0_0_, member0_.AGE as age2_0_0_, member0_.NAME as name3_0_0_ from member member0_ where member0_.ID=?
10:42:04.793 [main] DEBUG org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator - No actions specified; doing nothing
10:42:04.793 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator - No JtaPlatform was specified, checking resolver
10:42:04.793 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformResolverInitiator - No JtaPlatformResolver was specified, using default [org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver]
10:42:04.793 [main] DEBUG org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver - Could not resolve JtaPlatform, using default [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
10:42:04.809 [main] INFO org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
10:42:04.809 [main] DEBUG org.hibernate.query.spi.NamedQueryRepository - Checking 0 named HQL queries
10:42:04.809 [main] DEBUG org.hibernate.query.spi.NamedQueryRepository - Checking 0 named SQL queries
10:42:04.809 [main] DEBUG org.hibernate.service.internal.SessionFactoryServiceRegistryImpl - EventListenerRegistry access via ServiceRegistry is deprecated.  Use `sessionFactory.getEventEngine().getListenerRegistry()` instead
10:42:04.809 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Initializing SessionFactoryRegistry : org.hibernate.internal.SessionFactoryRegistry@21bd20ee
10:42:04.809 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Registering SessionFactory: d4075c4c-a2d4-43ac-b856-2734667285bc (<unnamed>)
10:42:04.809 [main] DEBUG org.hibernate.internal.SessionFactoryRegistry - Not binding SessionFactory to JNDI, no JNDI name configured
10:42:04.862 [main] DEBUG org.hibernate.stat.internal.StatisticsInitiator - Statistics initialized [enabled=false]
10:42:04.862 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == false
10:42:04.862 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - begin
10:42:04.878 [main] DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: 2, using strategy: org.hibernate.id.Assigned
찾은 객체: Member(id=2, name=가나다, age=26)
10:42:04.909 [main] DEBUG org.hibernate.hql.internal.QueryTranslatorFactoryInitiator - QueryTranslatorFactory: org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory@2abe9173
10:42:04.940 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - parse() - HQL: select m from com.example.ch02jpastart1.jpabook.start.Member m
10:42:04.940 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - Keyword  'Member' is being interpreted as an identifier due to: expecting IDENT, found 'Member'
10:42:04.940 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
10:42:04.940 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - --- HQL AST ---
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

10:42:04.962 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select << begin [level=1, statement=select]
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.tree.FromElement - FromClause{level=1} : com.example.ch02jpastart1.jpabook.start.Member (m) -> member0_
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.tree.FromReferenceNode - Resolved : m -> member0_.ID
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select : finishing up [level=1, statement=select]
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.HqlSqlWalker - processQuery() :  ( SELECT ( {select clause} member0_.ID ) ( FromClause{level=1} member member0_ ) )
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.util.JoinProcessor - Tables referenced from query nodes:
 \-QueryNode
    +-SelectClause
    | referencedTables(entity Member): [member]
    |  +-IdentNode
    |  | persister: SingleTableEntityPersister(com.example.ch02jpastart1.jpabook.start.Member)
    |  | originalText: m
    |  \-SqlFragment
    \-FromClause
       \-FromElement

10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.util.JoinProcessor - Using FROM fragment [member member0_]
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.antlr.HqlSqlBaseWalker - select >> end [level=1, statement=select]
10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - --- SQL AST ---
 \-[SELECT] QueryNode: 'SELECT'  querySpaces (member)
    +-[SELECT_CLAUSE] SelectClause: '{select clause}'
    |  +-[ALIAS_REF] IdentNode: 'member0_.ID as id1_0_' {alias=m, className=com.example.ch02jpastart1.jpabook.start.Member, tableAlias=member0_}
    |  \-[SQL_TOKEN] SqlFragment: 'member0_.AGE as age2_0_, member0_.NAME as name3_0_'
    \-[FROM] FromClause: 'from' FromClause{level=1, fromElementCounter=1, fromElements=1, fromElementByClassAlias=[m], fromElementByTableAlias=[member0_], fromElementsByPath=[], collectionJoinFromElementsByPath=[], impliedElements=[]}
       \-[FROM_FRAGMENT] FromElement: 'member member0_' FromElement{explicit,not a collection join,not a fetch join,fetch non-lazy properties,classAlias=m,role=null,tableName=member,tableAlias=member0_,origin=null,columns={,className=com.example.ch02jpastart1.jpabook.start.Member}}

10:42:04.978 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
10:42:04.994 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - HQL: select m from com.example.ch02jpastart1.jpabook.start.Member m
10:42:04.994 [main] DEBUG org.hibernate.hql.internal.ast.QueryTranslatorImpl - SQL: select member0_.ID as id1_0_, member0_.AGE as age2_0_, member0_.NAME as name3_0_ from member member0_
10:42:04.994 [main] DEBUG org.hibernate.hql.internal.ast.ErrorTracker - throwQueryException() : no errors
10:42:05.009 [main] DEBUG org.hibernate.SQL - 
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
10:42:05.009 [main] DEBUG org.hibernate.loader.Loader - Result set row: 0
10:42:05.025 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch02jpastart1.jpabook.start.Member#1]
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Resolving attributes for [com.example.ch02jpastart1.jpabook.start.Member#1]
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Processing attribute `age` : value = 12
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Attribute (`age`)  - enhanced for lazy-loading? - false
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Processing attribute `name` : value = 홍길동
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Attribute (`name`)  - enhanced for lazy-loading? - false
10:42:05.025 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Done materializing entity [com.example.ch02jpastart1.jpabook.start.Member#1]
지금 저장된 모든 데이터를 조회=[Member(id=1, name=홍길동, age=12)]
10:42:05.025 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - committing
10:42:05.025 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Processing flush-time cascades
10:42:05.025 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Dirty checking collections
10:42:05.041 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 insertions, 0 updates, 0 deletions to 1 objects
10:42:05.041 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 (re)creations, 0 updates, 0 removals to 0 collections
10:42:05.041 [main] DEBUG org.hibernate.internal.util.EntityPrinter - Listing entities:
10:42:05.041 [main] DEBUG org.hibernate.internal.util.EntityPrinter - com.example.ch02jpastart1.jpabook.start.Member{name=홍길동, id=1, age=12}
10:42:05.041 [main] DEBUG org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl - Initiating JDBC connection release from afterTransaction
10:42:05.041 [main] DEBUG org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl - Initiating JDBC connection release from afterTransaction
10:42:05.041 [main] DEBUG org.hibernate.internal.SessionFactoryImpl - HHH000031: Closing
10:42:05.041 [main] DEBUG org.hibernate.type.spi.TypeConfiguration$Scope - Un-scoping TypeConfiguration [org.hibernate.type.spi.TypeConfiguration$Scope@6abdec0e] from SessionFactory [org.hibernate.internal.SessionFactoryImpl@7ca0863b]
10:42:05.041 [main] DEBUG org.hibernate.service.internal.AbstractServiceRegistryImpl - Implicitly destroying ServiceRegistry on de-registration of all child ServiceRegistries
10:42:05.041 [main] INFO org.hibernate.orm.connections.pooling - HHH10001008: Cleaning up connection pool [jdbc:h2:tcp://localhost/~/test]
10:42:05.041 [main] DEBUG org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl - Implicitly destroying Boot-strap registry on de-registration of all child ServiceRegistries

Process finished with exit code 0
```

3. entityManager.close()

- 영속성 컨텍스트 종료

```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class DetachTestByClose {  
    public static void main(String[] args) {  
        //1.EntityManagerFactory 객체 생성  
  EntityManagerFactory emf=  
                Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체 생성  
  EntityManager em=  
                emf.createEntityManager();  
  //3.EntityTransaction 객체 생성  
  EntityTransaction tx=  
                em.getTransaction();  
  
 try {  
            //4.트랜잭션 시작  
  tx.begin();  
  //5.로직 테스트  
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  Member find=em.find(Member.class,1);  
  tx.commit();  
  System.out.println("find: "+find);  
  //////////////////////////////////////////////////////////////////////////////////////////////////  
 //6.로직테스트-영속성 컨텍스트 종료  
  em.close();  
  }catch (Exception e){  
            e.printStackTrace();  
  }  
        emf.close();  
  }  
  
  
}
```

entityManager.close를 하게 되면, 영속 managed 상태였던 객체가 이제는 영속성 컨텍스트가 아예 종료되게 되었기 때문에 준영속 detached 상태가 된다!

✅ 개발자가 직접 영속상태의 엔티티를 준영속상태로 만드는 것은 드물다고 한다!

#### 03-2-2. 준영속 detached 상태의 특징

1. `거의 비영속 new/transient 상태에 가까움`
2. `1`과 차이점이라고 한다면, `비영속 상태와 달리 식별자 값을 가지고 있다는 것`이라고 할 수 있다
3. 지연 로딩 lazy loading을 할 수 없다!
- 지연 로딩: 실제 객체 대신 프록시 객체를 로딩해두고 , `해당 객체를 실제 사용할 때 영속성 컨텍스트를 통해 데이터를 불러오는 방법`

- 지연로딩이 영속성 컨텍스트를 사용하기 때문에, 영속성 컨텍스트를 종료/초기화 하는 개념의 준영속상태에서는 당연히 지연로딩을 사용할 수 없다!!

### 03-3. `준영속 상태에서 다시 영속 상태로!` Detached state to Managed state!!

#### 03-3-1. 병합 : merge()

- 준영속 상태를 다시 영속 상태로 변환해서 엔티티를 영속성 컨텍스트가 관리할 수 있도록 하기 위해서는 `병합 merge`를 진행하면 된다!!

```java
public <T> merge(T entity);
```

```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class MergeTest {  
    //1.EntityManagerFactory 객체  
  static EntityManagerFactory emf=  
            Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체  
  static EntityManager em=  
            emf.createEntityManager();  
  //3.EntityTransaction 객체  
  static EntityTransaction tx=  
            em.getTransaction();  
  
 public static void main(String[] args) {  
        try{  
            //4.트랜잭션 시작  
  tx.begin();  
  //5.영속성 컨텍스트에 등록  
  Member member=new Member();  
  member.setId(4);  
  member.setName("가길동");  
  member.setAge(30);  
  
  em.persist(member);  
  //6.저장된 모든 데이터 확인  
  List<Member> members=  
                    em.createQuery("select m from Member m",Member.class)  
                            .getResultList();  
  System.out.println("영속성 컨텍스트 등록: "+members);  
  //7.영속성 컨텍스트 초기화 -> 준영속상태로 만들기  
  em.clear();  
  //8.준영속상태에서 다시 영속상태로 만들기  
  //일부 값 변경  
  member.setName("수정된 가길동");  
  //merge  
  em.merge(member);  
  //저장된 모든 데이터 다시 확인  
  members=  
                    em.createQuery("select m from Member m",Member.class)  
                            .getResultList();  
  System.out.println("준영속 상태에서 다시 영속 상태로 만들기: "+members);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            em.close();  
  }  
        emf.close();  
  }  
}
```
위와 같이 원래는 clear 등으로 준영속 상태로 만들었는데, 그 이후에 변경을 하게 되면, 원래는! 변경감지가 되지 않는다. 하지만 이 경우에는 merge(엔티티)를 통해서 특정 객체에 대해서 다시 영속 상태로의 전환이 이루어지면서, 변경감지가 이루어진 모습을 아래처럼 확인해볼 수 있다

```
11:31:24.049 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Done materializing entity [com.example.ch02jpastart1.jpabook.start.Member#3]
영속성 컨텍스트 등록: [Member(id=1, name=홍길동, age=12), Member(id=2, name=가길동, age=27), Member(id=3, name=나길동, age=29), Member(id=4, name=가길동, age=30)]
(중략)
11:31:24.095 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Done materializing entity [com.example.ch02jpastart1.jpabook.start.Member#3]
준영속 상태에서 다시 영속 상태로 만들기: [Member(id=1, name=홍길동, age=12), Member(id=2, name=가길동, age=27), Member(id=3, name=나길동, age=29), Member(id=4, name=수정된 가길동, age=30)]
```
1. 준영속 상태로 변환된 후(clear), 1차 캐시 엔티티를 찾고자 하지만, 영속성 컨텍스트 내에 엔티티가 없게 된 상태이므로, 더는 수정사항이 db에 반영될 수 없다
2. 영속 상태의 새로운 member 엔티티 객체가 merge 메서드를 통해서 반환됨

### 03-4. 비영속 병합

`merge(entity)`는 비영속 엔티티도 영속 상태로 만들 수 있다!
먼저 간단하게 엔티티 객체를 하나 만들고, merge 메서드를 활용해주자
```java
package com.example.ch02jpastart1.jpabook.start;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class TransientToManagedTest {  
    /**  
 * @author JISOOJEONG  
 * merge(entity) 메서드를 활용해서 비영속 상태의 엔티티를  
  * 영속상태로 만들기  
  * */  
  //1.EntityManagerFactory 객체  
  static EntityManagerFactory emf=  
            Persistence.createEntityManagerFactory("jpabook");  
  //2.EntityManager 객체  
  static EntityManager em=  
            emf.createEntityManager();  
  //3.EntityTransaction 객체  
  static EntityTransaction tx=  
            em.getTransaction();  
  
 public static void main(String[] args) {  
        try{  
            //4.트랜잭션 시작  
  tx.begin();  
  //5.비영속 엔티티를 영속 상태로 변경해주기  
  Member member=new Member();  
  member.setId(5);  
  member.setAge(22);  
  member.setName("def");  
  //6.new/transient state to managed state  
  em.merge(member);  
  //7.저장된 모든 데이터 확인해보기  
  List<Member> members=  
                    em.createQuery("select m from Member m",Member.class)  
                            .getResultList();  
  System.out.println("비영속 상태에서 영속 상태로 변경하기: "+members);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            em.close();  
  }  
        emf.close();  
  }  
}
```

그러면 이제는 다음과 같이 비영속이었던 엔티티 객체가 영속상태가 된 것을 알 수 있다

```
11:45:34.134 [main] DEBUG org.hibernate.engine.internal.TwoPhaseLoad - Done materializing entity [com.example.ch02jpastart1.jpabook.start.Member#4]
비영속 상태에서 영속 상태로 변경하기: [Member(id=1, name=홍길동, age=12), Member(id=2, name=가길동, age=27), Member(id=3, name=나길동, age=29), Member(id=4, name=수정된 가길동, age=30), Member(id=5, name=def, age=22)]

```



