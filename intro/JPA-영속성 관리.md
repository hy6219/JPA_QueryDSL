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


