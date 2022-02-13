# [JPA] 고급 매핑

## 1. 상속 관계 매핑
![데이터베이스-슈퍼타입 서브타입 관계 Super-Type Sub-Type Relationship](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/db_%EC%8A%88%ED%8D%BC%ED%83%80%EC%9E%85_%EC%84%9C%EB%B8%8C%ED%83%80%EC%9E%85_%EB%85%BC%EB%A6%AC%EB%AA%A8%EB%8D%B8.jpg?raw=true)

RDBMS에는 객체지향언어에서 다루는 `상속` 개념이 없다!
대신 슈퍼타입-서브타입 관계라는 모델링 기법이 객체의 상속 개념과 가장 유사하다

![객체상속모델](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/orm_%EA%B0%9D%EC%B2%B4%EC%83%81%EC%86%8D%EB%AA%A8%EB%8D%B8.jpg?raw=true)

✅`슈퍼타입 서브타입 논리모델을 실제 물리모델인 테이블로 구현하는 방법`

- `조인전략` : 각각의 테이블로 변환
- `단일 테이블 전략` : 통합 테이블로 변환
- `구현 클래스마다 테이블 전략` : 서브타입 테이블로 변환

### 1-1. 조인전략 Joined Strategy

![슈퍼타입 서브타입 논리모델을 실제 테이블로 구현하는 방법- 조인전략](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EC%83%81%EC%86%8D%EA%B4%80%EA%B3%84%EB%A7%A4%ED%95%91_%EC%A1%B0%EC%9D%B8%EC%A0%84%EB%9E%B5.jpg?raw=true)

`엔티티 각각을 모두 테이블로` 만들고,
`자식 테이블이 부모 테이블의 기본키를 받아서 기본키+외래키로 사용하는 전략`

➡ 조회할 때 조인을 자주 사용

(단점)

- 객체는 타입으로 구분할 수 있지만, `테이블을 구분하는 컬럼을 추가`해야!
(이러한 목적으로 Item 테이블에 DTYPE 컬럼을 추가했다)

위와 같은 구조를 jpa로 구현해주면 아래와 같이 
Item 엔티티를 상속받는 Album, Movie, Book 엔티티로 생각해볼 수 있다


```java
package com.example.ch07jpastart1.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.*;  
  
@Entity  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Inheritance(strategy = InheritanceType.JOINED)  
@DiscriminatorColumn(name = "DTYPE")  
public class Item {  
    @Id  
 @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "item_id")  
    private Long id;  
  
    private String name;  
  
    private int price;  
}
```

```java
package com.example.ch07jpastart1.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.EqualsAndHashCode;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
  
@Entity  
@EqualsAndHashCode(callSuper = false)  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@DiscriminatorValue("A")  
public class Album extends Item{  
    private String artist;  
}
```

```java
package com.example.ch07jpastart1.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.EqualsAndHashCode;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
  
/**  
 * warning: Generating equals/hashCode implementation but without a call to superclass, * even though this class does not extend java.lang.Object. If this is intentional, * add '@EqualsAndHashCode(callSuper=false)' to your type. */@Entity  
@EqualsAndHashCode(callSuper = false)  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@DiscriminatorValue("M")  
public class Movie extends Item {  
    private String director;  
    private String actor;  
}
```


```java
package com.example.ch07jpastart1.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.EqualsAndHashCode;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
  
@Entity  
@EqualsAndHashCode(callSuper = false)  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@DiscriminatorValue("B")  
public class Book extends Item{  
    private String author;  
    private String isbn;  
}
```


