# 왜 JPA 일까?

## 01. 기존의 웹 어플리케이션에서 SQL을 직접 다룰 때 발생하는 문제점

- 진정한 의미의 계층분할이 어려움
- 엔티티를 신뢰할 수 없음
- SQL에 의존적인 개발을 피하기 어려움

## 02. JPA가 도입되지 않은 상황에서 발생가능한 이슈

1.  `기존의 웹 어플리케이션에서 SQL을 직접 다룰 때 발생하는 문제점`

2. 패러다임의 불일치

- RDBMS는 데이터 중심으로 구조화되어 있고, 집합적인 사고를 요구
- RDBMS는 객체지향에서 이야기하는 추상화, 상속, 다형성과 같은 개념이 없음

▶ 다음과 같이 상속의 측면을 생각해보면, 실제 상속관계에 있는 두 객체가 있다면 Album 클래스의 경우, ALBUM과 ITEM 모두에 대한 SQL을 고려해야 한다

```java
abstract class Item{
	Long id;
	String name;
	int price;
}
class Album extends Item{
	String artist;
}

class Movie extends Item{
	String director;
	String actor;
}

class Book extends Item{
	String author;
	String isbn;
}
```

이경우 insert 해야할 경우 다음과 같이 두 테이블을 고려해야 한다▶
 ```sql
INSERT INTO ITEM...
INSERT INTO ALBUM...
```

3. 객체는 참조에 접근해서 연관된 객체를 조회 vs 테이블은 외래키를 사용해서 조인을 활용해서 연관된 테이블을 조회

- 객체: 참조가 있는 방향으로만 조회 가능
▶ 참조가 있는 방향: 포함된 객체 방향으로
```java
Member{
 Team
 }
```
이라면
↔ member.getTeam()은 가능하지만, team.getMember()는 불가능

vs

- 테이블: 외래키 하나로 MEMBER JOIN TEAM, TEAM JOIN MEMBER도 가능!

`객체 그래프 탐색`

- 참조를 사용해서 연관된 객체를 찾는 것
- JPA는 객체 그래프를 마음껏 탐색 가능
▶ `지연로딩` : 연관된 객체를 사용하는 시점까지 DB 조회를 미루게 되는 특징!
덕분에 지연로딩을 사용 가능!

4. DB와 객체의 비교방식 차이
- DB: 기본키값으로 각 로우를 비교
- 객체: 동일성(주소값 비교) 및 동등성(값 비교) 비교

▶ 객체에서는 매번 인스턴스가 새로 생성되기 때문

## 03. (정리) 왜 JPA를 사용해야 할까?

1. 생산성 증가(DB 설계 중심 패러다임을 객체 설계 중심으로 역전 가능)
2. 유지보수 코드 수 감소
3. 패러다임 불일치 해결
4. 애플리케이션-DB 사이에서 다양한 성능 최적화 제공
5. 데이터 접근 추상화, 벤더 독립성

## 04. 그래서 JPA가 뭔가요?

`자바 ORM 기술에 대한 API 표준 명세` 다!

EJB 3.0에서 하이버네이트를 기반으로 새로운 자바 ORM 기술 표준으로 발표된 기술!

하이버네이트: JPA의 구현체!

