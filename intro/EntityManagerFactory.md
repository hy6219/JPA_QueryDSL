# EntityManagerFactory 클래스

## 01 . What is `EntityManagerFactory`?

퍼시스턴스 유닛을 위한 엔티티 매니저와 상호작용하기 위한 인터페이스

✅ (1) 애플리케이션이 entity manager facory를 사용하는 것이 끝났을 때

(2) 애플리케이션이 셧다운되었을 때

➡ 애플리케이션은 entity manager factory를 닫는다!

★ EntityManagerFactory가 닫히게 되었을때, entity manager들은 닫힌 상태로 고려된다!!

EntityManagerFactory와 관련있는 객체가 바로 EntityManager인데,
이 EntityManagerFactory는 `Persistence Context`와 관련있는 객체인 반면, `EntityManager`는 퍼시스턴스 엔티티 객체를 생성하고 제거하는 데에 사용됨

## 02. JPA/Hibernate Persistence Context

[JPA/Hibernate Persistence Context](https://www.baeldung.com/jpa-hibernate-persistence-context)

`고유한 엔티티 객체가 존재하는 어떤 퍼시스턴스 계층의 엔티티 속성의 모임`

> A persistence context is a set of entity instances in which for any persistent entity identity there is a unique entity instance. [참조](https://www.baeldung.com/jpa-hibernate-persistence-context)

(1) DB로부터 불러오거나
(2) DB에 저장된

`모든 엔티티 객체들이 위치하는 first level cache`

### 02-1. What is `first-level cache` and `second-level cache`?

[Persistence Context 관련 내용 중 1차,2차 캐시 관련 내용 참고글](https://velog.io/@dnjscksdn98/JPA-Hibernate-First-Level-Cache-Second-Level-Cache)

<SqlSession이 전달되는 단계>

`Persistence Context` ➡ `First Level Cache(1차캐시)` ➡`Second Level Cache(2차 캐시)` ➡ DB

(1) 1차 캐시

- 영속성 컨텍스트 내부에서 엔티티 매니저로 조회하거나 변경(DQL, DML 작업)하는 `모든 엔티티가 저장되는 장소`

(2) 2차 캐시

- 애플리케이션 단위의 캐시
- 애플리케이션 종료하는 시점까지 캐시가 유지됨
- 데이터 조회시, 우선적으로 2차캐시에 데이터가 존재하는지를 먼저 확인하기 때문에 DB 조회 횟수를 획기적으로 줄일 수 있음

#### 02-1-1. Hibernate 에서 지원되는 캐시 타입

[Hibernate 에서 지원되는 캐시 타입(https://www.educba.com/caching-in-hibernate/)

Hibernate에서는 아래와 같이 크게 세 가지 타입의 캐시 타입이 존재한다!

1. First-Level Caching=Session Cache

- `Session` 객체와 연관있는 캐시
- 하이버네이트에서 기본적으로 제공되는 필수(의무)적인 캐시
- 모든 요청 객체들은 이 캐시를 거치게 되고, 애플리케이션에서 `많은 세션 객체`들을 보냄으로써 이용됨
- 한번 세션이 종료되면, 이 캐시 또한 clear되고, 객체들은 자기 자신에 대한 보유를 지속

2. Second-Level Caching

- `SessionFactory` 객체와 연관있는 캐시
- First Level Caching에서 검사된 데이터를 찾을 수 없는 경우에 사용

3. Query Caching

- 쿼리를 통해 반환되는 객체들의 키값을 보다 정확하게 처리하고, 결과를 캐싱해줌







