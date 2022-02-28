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

```java
package com.example.ch07jpastart1.test;  
  
import com.example.ch07jpastart1.domain.entity.Album;  
import com.example.ch07jpastart1.domain.entity.Item;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
  
public class JoinedStrategyTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory =  
                Persistence.createEntityManagerFactory("jpabook");  
        EntityManager entityManager =  
                entityManagerFactory.createEntityManager();  
        EntityTransaction tx =  
                entityManager.getTransaction();  
  
        try {  
            tx.begin();  
            logic(entityManager);  
            tx.commit();  
        }catch (Exception e){  
            e.printStackTrace();  
        }finally {  
            entityManager.close();  
        }  
        entityManagerFactory.close();  
    }  
  
    static void logic(EntityManager entityManager){  
        Item item = new Item();  
        item.setName("item1");  
        item.setPrice(20000);  
        entityManager.persist(item);  
  
        Album album = new Album();  
        album.setArtist("artist");  
        album.setName("abc");  
        album.setPrice(30000);  
        entityManager.persist(album);  

		List<Item> items = entityManager.createQuery("select item from Item item",Item.class)  
        .getResultList();  
  
		System.out.println("find all items : "+items);
    }  
}
```
그리고 위와 같이 앨범과 아이템 엔티티를 영속성 컨텍스트에 등록하고 Item을 조회해보면 
```
Hibernate: 
    
    create table Album (
       artist varchar(255),
        item_id bigint not null,
        primary key (item_id)
    )
    create table Book (
       author varchar(255),
        isbn varchar(255),
        item_id bigint not null,
        primary key (item_id)
    )
   create table Movie (
       actor varchar(255),
        director varchar(255),
        item_id bigint not null,
        primary key (item_id)
    ) 
   create table Item (
       DTYPE varchar(31) not null,
        item_id bigint generated by default as identity,
        name varchar(255),
        price integer not null,
        primary key (item_id)
    )    
    
    alter table Album 
       add constraint FK53gjpcnqq4ham6n200xsi04me 
       foreign key (item_id) 
       references Item   
  
   alter table Book 
       add constraint FK282k6114lkwimf5inj4oeyvuy 
       foreign key (item_id) 
       references Item
    alter table Movie 
       add constraint FK77rr749acgm001t9th29xusam 
       foreign key (item_id) 
       references Item       
13:41:16.822 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Item
        (item_id, name, price, DTYPE) 
    values
        (null, ?, ?, 'Item')
     

13:41:16.835 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Item
        (item_id, name, price, DTYPE) 
    values
        (null, ?, ?, 'A')
13:41:16.839 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Album
        (artist, item_id) 
    values
        (?, ?)

13:41:17.032 [main] DEBUG org.hibernate.SQL - 
    select
        item0_.item_id as item_id2_2_,
        item0_.name as name3_2_,
        item0_.price as price4_2_,
        item0_1_.artist as artist1_0_,
        item0_2_.actor as actor1_3_,
        item0_2_.director as director2_3_,
        item0_3_.author as author1_1_,
        item0_3_.isbn as isbn2_1_,
        item0_.DTYPE as dtype1_2_ 
    from
        Item item0_ 
    left outer join
        Album item0_1_ 
            on item0_.item_id=item0_1_.item_id 
    left outer join
        Movie item0_2_ 
            on item0_.item_id=item0_2_.item_id 
    left outer join
        Book item0_3_ 
            on item0_.item_id=item0_3_.item_id
Hibernate: 
    select
        item0_.item_id as item_id2_2_,
        item0_.name as name3_2_,
        item0_.price as price4_2_,
        item0_1_.artist as artist1_0_,
        item0_2_.actor as actor1_3_,
        item0_2_.director as director2_3_,
        item0_3_.author as author1_1_,
        item0_3_.isbn as isbn2_1_,
        item0_.DTYPE as dtype1_2_ 
    from
        Item item0_ 
    left outer join
        Album item0_1_ 
            on item0_.item_id=item0_1_.item_id 
    left outer join
        Movie item0_2_ 
            on item0_.item_id=item0_2_.item_id 
    left outer join
        Book item0_3_ 
            on item0_.item_id=item0_3_.item_id

13:41:17.037 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart1.domain.entity.Item#2]
find all items : [Item(id=1, name=item1, price=20000), Album(artist=artist)]
```
위와 같이 Item 엔티티에서 item 객체 뿐 아니라 album 객체에 대해서  DTYPE  컬럼값에 대해서 "A"로 Item 엔티티에 저장되고, Album 엔티티도 album 데이터가 저장되는 것을 확인해볼 수 있다

![조인전략](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EC%A1%B0%EC%9D%B8%EC%A0%84%EB%9E%B5.PNG?raw=true)

✅ `@Inheritance(strategy=InheritanceType.JOINED)` :

- 부모 클래스에 `@Inheritance`를 붙여야 함(상속 매핑)
- `strategy=InheritanceType.JOINED`: 조인 전략


✅ `@DiscriminatorColumn(name = "DTYPE")`:

- 부모 클래스에 구분 컬럼을 지정 ➡ 자식 테이블을 구분 가능
- name 기본값은 DTYPE

✅ `@DiscriminatorValue("M")` :

- 엔티티에 저장할 때 구분 컬럼에 입력할 값을 저장

✅ `@PrimaryKeyJoinColumn(name=~)`

- PK는 슈퍼타입 테이블의 아이디값을 사용하는데, 서브타입 테이블 내에서의 PK이면서 FK인 컬럼명이 변경됨

Album 클래스만 아래와 같이 변경해보자
```java
package com.example.ch07jpastart1.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.EqualsAndHashCode;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
import javax.persistence.PrimaryKeyJoinColumn;  
  
@Entity  
@EqualsAndHashCode(callSuper = false)  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@DiscriminatorValue("A")  
@PrimaryKeyJoinColumn(name ="ALBUM_ID")  
public class Album extends Item{  
    private String artist;  
}
```

그러면 외래키 참조 제약에 대해서 아래와 같이 변경되는 것을 확인해볼 수 있다
```
 alter table Album 
       add constraint FK2l2x75jwq4vybwvphuc6mu7kw 
       foreign key (ALBUM_ID) 
       references Item
```
![조인전략-`@PrimaryJoinColumn`](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EC%A1%B0%EC%9D%B8%EC%A0%84%EB%9E%B5%20%60@PrimaryJoinColumn%60.PNG?raw=true)



✅✅ `조인 전략의 장점`

- 테이블이 정규화됨(한 컬럼 내에서 값이 여러개가 아니고, 어떤 컬럼이 다른 컬럼에 영향을 미치지도 않음)
- 외래키 참조 무결성 제약조건 활용 가능
- 저장공간을 효율적으로 사용

✅✅ `조인 전략의 단점`

- 조회할 경우 조인이 많아짐으로써 성능이 저하될 수 있음
- 조회 쿼리가 복잡
- 데이터 등록시 insert sql을 두 번 실행됨(부모, 자식 클래스)

✅✅ `조인 전략의 특징`

- 하이버네이트 등 몇몇 구현체는 `@DiscriminationColumn 없이도 동작`

### 1-2. 단일 테이블 전략 Single-Table Strategy

조인전략과 단일 테이블 전략은 공통적으로 "구분 컬럼"을 두지만, 차이점이라면, 단일 테이블 전략은 db상으로 부모 엔티티 하나만을 두고 관리한다는 점이 차이점이라고 생각한다!!(그렇기 때문에 부모 클래스에서 모든 자식 클래스의 컬럼을 갖고 있게 된다)

![상속매핑- 단일 테이블 전략 Single-Table Strategy](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EC%83%81%EC%86%8D%EA%B4%80%EA%B3%84%EB%A7%A4%ED%95%91_%EB%8B%A8%EC%9D%BC%ED%85%8C%EC%9D%B4%EB%B8%94%EC%A0%84%EB%9E%B5.jpg?raw=true)

엔티티는 위에서와 동일하게 Item, Album, Book, Movie로 구성하는데, 이번에는 모든 컬럼들이 Item 엔티티에서 관리되기 때문에

Album, Book, Movie에서 이번 경우에 따로 추가할 것은 없다
다만, 주의할 점은 `@Data`를 이용할 때, `@EqualsAndHashCode(callSuper=false)`를 붙여줘야 한다

- 그 이유는 "warning: Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type."와 같이 문구를 볼 수 있게 되기 때문인데, 이때에는 따로 구현한 Value Object가 없기 때문이라고 한다

간단하게 단일 테이블 전략을 이용해서 Item 엔티티를 관리해주고, Item 엔티티와 Album 엔티티를 저장하고 조회해보자

```java
package com.example.ch07jpastart2.domain.entity;  
  
import lombok.AllArgsConstructor;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
import javax.persistence.*;  
  
@Entity  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
@DiscriminatorColumn(name ="DTYPE")  
@TableGenerator(  
        name = "item_table_generator",  
        pkColumnValue = "item_sequences",  
        allocationSize = 1  
)  
public class Item {  
    @Id  
 @GeneratedValue(strategy = GenerationType.TABLE, generator = "item_table_generator")  
    private Long id;  
  
    private String name;  
    private int price;  
    private String artist;  
    private String director;  
    private String actor;  
    private String author;  
    private String isbn;  
}
```

```java
package com.example.ch07jpastart2.domain.entity;  
  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
import javax.persistence.PrimaryKeyJoinColumn;  
  
@Entity  
@NoArgsConstructor  
@DiscriminatorValue("album")  
@PrimaryKeyJoinColumn(name ="ALBUM_ID")  
public class Album extends Item{  
  
}
```

```java
package com.example.ch07jpastart2.domain.entity;  
  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
import javax.persistence.PrimaryKeyJoinColumn;  
  
@Entity  
@NoArgsConstructor  
@DiscriminatorValue("movie")  
@PrimaryKeyJoinColumn(name ="MOVIE_ID")  
public class Movie extends Item{  
}
```

```java
package com.example.ch07jpastart2.domain.entity;  
  
import lombok.NoArgsConstructor;  
  
import javax.persistence.DiscriminatorValue;  
import javax.persistence.Entity;  
import javax.persistence.PrimaryKeyJoinColumn;  
  
@Entity  
@NoArgsConstructor  
@DiscriminatorValue("book")  
@PrimaryKeyJoinColumn(name ="BOOK_ID")  
public class Book extends Item{  
}
```

```java
package com.example.ch07jpastart2.test;  
  
import com.example.ch07jpastart2.domain.entity.Album;  
import com.example.ch07jpastart2.domain.entity.Item;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class SingleTableStrategyTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory =  
                Persistence.createEntityManagerFactory("jpabook");  
        EntityManager entityManager =  
                entityManagerFactory.createEntityManager();  
        EntityTransaction tx =  
                entityManager.getTransaction();  
  
        try {  
            tx.begin();  
            logic(entityManager);  
            tx.commit();  
        }catch (Exception e){  
            e.printStackTrace();  
        }finally {  
            entityManager.close();  
        }  
        entityManagerFactory.close();  
    }  
  
    static void logic(EntityManager entityManager){  
        Item item = new Item();  
        item.setName("item1");  
        item.setPrice(20000);  
        entityManager.persist(item);  
  
        Album album = new Album();  
        album.setArtist("album1");  
        entityManager.persist(album);  
  
        List<Item> items = entityManager.createQuery("select item from Item item",Item.class)  
                .getResultList();  
  
        System.out.println("find all items (single table strategy): "+items);  
  
        List<Album> albums = entityManager.createQuery("select album from Album album",Album.class)  
                .getResultList();  
        System.out.println("find all albums: "+albums);  
    }  
}
```

```
    create table Item (
       DTYPE varchar(31) not null,
        id bigint not null,
        actor varchar(255),
        artist varchar(255),
        author varchar(255),
        director varchar(255),
        isbn varchar(255),
        name varchar(255),
        price integer not null,
        primary key (id)
    )
Hibernate: 
    insert 
    into
        Item
        (actor, artist, author, director, isbn, name, price, DTYPE, id) 
    values
        (?, ?, ?, ?, ?, ?, ?, 'Item', ?)
Hibernate: 
    insert 
    into
        Item
        (actor, artist, author, director, isbn, name, price, DTYPE, id) 
    values
        (?, ?, ?, ?, ?, ?, ?, 'album', ?)
Hibernate: 
    select
        item0_.id as id2_0_,
        item0_.actor as actor3_0_,
        item0_.artist as artist4_0_,
        item0_.author as author5_0_,
        item0_.director as director6_0_,
        item0_.isbn as isbn7_0_,
        item0_.name as name8_0_,
        item0_.price as price9_0_,
        item0_.DTYPE as dtype1_0_ 
    from
        Item item0_
15:10:39.412 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart2.domain.entity.Item#2]
find all items (single table strategy): [Item(id=1, name=item1, price=20000, artist=null, director=null, actor=null, author=null, isbn=null), Item(id=2, name=null, price=0, artist=album1, director=null, actor=null, author=null, isbn=null)]
Hibernate: 
    select
        album0_.id as id2_0_,
        album0_.actor as actor3_0_,
        album0_.artist as artist4_0_,
        album0_.author as author5_0_,
        album0_.director as director6_0_,
        album0_.isbn as isbn7_0_,
        album0_.name as name8_0_,
        album0_.price as price9_0_ 
    from
        Item album0_ 
    where
        album0_.DTYPE='album'
15:10:39.462 [main] DEBUG org.hibernate.loader.Loader - Result set row: 0
15:10:39.462 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart2.domain.entity.Album#2]
find all albums: [Item(id=2, name=null, price=0, artist=album1, director=null, actor=null, author=null, isbn=null)]
```

그러면 위와 같이 테이블은 Item만 생성되고, 저장되고 조회되는 것 또한 Item 테이블을 활용해서 진행되는 모습을 확인해볼 수 있다

다만, 차이점은 Album 엔티티로 조회하고자 접근할 때에는 실질적으로는 Item 테이블을 이용해서 DTYPE 값이 "album"(DiscriminatorValue)일 경우를 조건으로 걸어서 조회해주는 것을 확인해볼 수 있다

![상속 매핑- 단일 테이블 전략](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EB%8B%A8%EC%9D%BC%ED%85%8C%EC%9D%B4%EB%B8%94%EC%A0%84%EB%9E%B5.PNG?raw=true)

✅✅ `단일 테이블 전략의 장점`

- 조인이 필요없기 때문에 일반적으로 `조회 성능이 빠르다`
- 조회쿼리가 단순

✅✅ `단일 테이블 전략의 단점`

- `자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야`
- 단일 테이블에 모든 것을 저장하기 때문에 테이블이 커질 수 있어서, 상황에 따라서는 조회 성능이 저하될 수 있음

✅✅ `단일 테이블 전략의 특징`

- 구분 컬럼을 꼭 사용!!➡ `@DiscriminatorColumn`을 꼭 명시!!
- `@DiscriminatorValue 를 미지정시`, 기본으로 엔티티 이름을 사용(ex: Movie, Album, Book)

### 1-3. 구현 클래스마다 테이블 전략 Table-per-Concrete-Class Strategy

앞에서 확인해본 조인전략이나 단일 테이블 전략과의 차이점은 `구분 컬럼없이` && `자식 엔티티에 공통적인 컬럼들과 각자의 컬럼들이 존재`한다는 점이다!(부모 엔티티는 개념상으로만 존재)

조인전략에서는 자식 테이블에 그저 부모식별자와 각자의 컬럼만 존재했던 것과는 확연히 다르고

단일 테이블 전략에서는 부모테이블만 존재하고 , 부모 테이블에서 자식 테이블 정보를 모두 포함하는 모습과도 확연히 다르다!!

![상속 매핑- 구현 클래스마다 테이블 전략](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EC%83%81%EC%86%8D%EA%B4%80%EA%B3%84%EB%A7%A4%ED%95%91_%EA%B5%AC%ED%98%84%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A7%88%EB%8B%A4%ED%85%8C%EC%9D%B4%EB%B8%94%EC%A0%84%EB%9E%B5.jpg?raw=true)

이번에는 

- 공통적인 부분들은 추상 클래스로 만들어서(일반 메서드도 활용하면서 공통되는 부분을 부여하기 위함⬅ 반드시 기억하자!! 추상 클래스는 생성자로 객체를 만들수 없다~ ) 관리하고
```java
public abstract class Clazz{
	//필드
	//생성자
	//일반,추상메서드
}
```
```java
//추상메서드
[public | protected] abstract 리턴타입 메서드명(매개변수);
```
- 자식 엔티티격인 Album, Book, Movie 엔티티는 이를 상속하도록 하자

그리고  Album, Book, Movie 엔티티를 저장하고 조회해보자

```java
package com.example.ch07jpastart3.domain.entity;  
  
import lombok.Data;  
  
import javax.persistence.*;  
  
@Entity  
@Data  
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)  
@TableGenerator(  
        name = "item_table_generator",  
        pkColumnValue = "item_sequences",  
        allocationSize = 1  
)  
public abstract class Item {  
    @Id  
 @Column(name ="item_id")  
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "item_table_generator")  
    private Long id;  
  
    private String name;  
    private int price;  
}
```

```java
package com.example.ch07jpastart3.domain.entity;  
  
import lombok.*;  
  
import javax.persistence.Entity;  
  
@Entity  
@Setter  
@Getter  
@ToString  
@NoArgsConstructor  
@AllArgsConstructor  
public class Album extends Item{  
    private String artist;  
}
```

```java
package com.example.ch07jpastart3.domain.entity;  
  
import lombok.*;  
  
import javax.persistence.Entity;  
import javax.persistence.PrimaryKeyJoinColumn;  
  
@Entity  
@Setter  
@Getter  
@ToString  
@NoArgsConstructor  
@AllArgsConstructor   
public class Book extends Item{  
    private String author;  
    private String isbn;  
}
```

```java
package com.example.ch07jpastart3.domain.entity;  
  
import lombok.*;  
  
import javax.persistence.Entity;  
  
@Entity  
@Setter  
@Getter  
@ToString  
@NoArgsConstructor  
@AllArgsConstructor  
public class Movie extends Item{  
    private String director;  
    private String actor;  
}
```

```java
package com.example.ch07jpastart3.test;  
  
import com.example.ch07jpastart3.domain.entity.Album;  
import com.example.ch07jpastart3.domain.entity.Book;  
import com.example.ch07jpastart3.domain.entity.Item;  
import com.example.ch07jpastart3.domain.entity.Movie;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class TablePerConcreteClassStrategyTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory =  
                Persistence.createEntityManagerFactory("jpabook");  
        EntityManager entityManager =  
                entityManagerFactory.createEntityManager();  
        EntityTransaction tx =  
                entityManager.getTransaction();  
        try {  
            tx.begin();  
            logic(entityManager);  
            tx.commit();  
        }catch (Exception e){  
            e.printStackTrace();  
        }finally {  
            entityManager.close();  
        }  
        entityManagerFactory.close();  
    }  
  
    static void logic(EntityManager entityManager){  
  
        Album album = new Album();  
        album.setName("album0");  
        album.setPrice(30000);  
        album.setArtist("album");  
        entityManager.persist(album);  
  
        Book book = new Book();  
        book.setName("book0");  
        book.setPrice(20000);  
        book.setAuthor("author0");  
        book.setIsbn("1234567");  
        entityManager.persist(book);  
  
        Movie movie = new Movie();  
        movie.setName("movie0");  
        movie.setPrice(20000);  
        movie.setActor("actor0");  
        movie.setDirector("director0");  
        entityManager.persist(movie);  
  
        List<Item> items = entityManager.createQuery("select item from Item item",Item.class)  
                .getResultList();  
        System.out.println("find all items: "+items);  
  
        Album findAlbum = entityManager.find(Album.class,1L);  
        Book findBook = entityManager.find(Book.class,2L);  
        Movie findMovie = entityManager.find(Movie.class,3L);  
  
        System.out.println("find album: "+findAlbum);  
        System.out.println("find book: "+findBook);  
        System.out.println("find movie: "+findMovie);  
    }  
}
```

그러면 

```
15:51:49.124 [main] DEBUG org.hibernate.SQL - 
    
    create table Album (
       item_id bigint not null,
        name varchar(255),
        price integer not null,
        artist varchar(255),
        primary key (item_id)
    )

15:51:49.197 [main] DEBUG org.hibernate.SQL - 
    
    create table Book (
       item_id bigint not null,
        name varchar(255),
        price integer not null,
        author varchar(255),
        isbn varchar(255),
        primary key (item_id)
    )

15:51:49.198 [main] DEBUG org.hibernate.SQL - 
    
    create table item_table_generator (
       sequence_name varchar(255) not null,
        next_val bigint,
        primary key (sequence_name)
    )

15:51:49.201 [main] DEBUG org.hibernate.SQL - 
    
    create table Movie (
       item_id bigint not null,
        name varchar(255),
        price integer not null,
        actor varchar(255),
        director varchar(255),
        primary key (item_id)
    )
15:51:49.564 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Album
        (name, price, artist, item_id) 
    values
        (?, ?, ?, ?)

15:51:49.575 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Book
        (name, price, author, isbn, item_id) 
    values
        (?, ?, ?, ?, ?)

15:51:49.577 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Movie
        (name, price, actor, director, item_id) 
    values
        (?, ?, ?, ?, ?)

15:51:49.583 [main] DEBUG org.hibernate.SQL - 
    select
        item0_.item_id as item_id1_2_,
        item0_.name as name2_2_,
        item0_.price as price3_2_,
        item0_.artist as artist1_0_,
        item0_.author as author1_1_,
        item0_.isbn as isbn2_1_,
        item0_.actor as actor1_3_,
        item0_.director as director2_3_,
        item0_.clazz_ as clazz_ 
    from
        ( select
            item_id,
            name,
            price,
            artist,
            null as author,
            null as isbn,
            null as actor,
            null as director,
            1 as clazz_ 
        from
            Album 
        union
        all select
            item_id,
            name,
            price,
            null as artist,
            author,
            isbn,
            null as actor,
            null as director,
            2 as clazz_ 
        from
            Book 
        union
        all select
            item_id,
            name,
            price,
            null as artist,
            null as author,
            null as isbn,
            actor,
            director,
            3 as clazz_ 
        from
            Movie 
    ) item0_

15:51:49.597 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart3.domain.entity.Item#3]
find all items: [Album(artist=album), Book(author=author0, isbn=1234567), Movie(director=director0, actor=actor0)]
find album: Album(artist=album)
find book: Book(author=author0, isbn=1234567)
find movie: Movie(director=director0, actor=actor0)
```

![상속 매핑- 구현 클래스마다 테이블 전략](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EA%B5%AC%ED%98%84%20%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A7%88%EB%8B%A4%20%ED%85%8C%EC%9D%B4%EB%B8%94%20%EC%A0%84%EB%9E%B5.PNG?raw=true)

그러면 위와 같이, album 등 각 엔티티가 만들어지고, 여러 자식 테이블을 함께 조회할 경우 , 모든 자식 엔티티들을 합하여 조회하는 것을 확인해볼 수 있다

✅✅ `구현 클래스마다 테이블 전략 장점`

- 서브 타입을 구분해서 처리할 때 효과적
- not null 제약조건 사용 가능

✅✅ `구현 클래스마다 테이블 전략 단점`

- 여러 자식 테이블을 함께 조회할 경우 성능이 느림
(UNION ALL 사용)

- 자식 테이블을 통합해서 쿼리하기 어려움

✅✅ `구현 클래스마다 테이블 전략 특징`

- 구분 컬럼 사용하지 않음

💥💥 이 방식은 DB 설계자와 ORM 전문가 모두 추천하지 않는 전략이라고 하니, 조인 전략이나 단일 테이블 전략을 고려하자!

## 2. `@MappedSuperclass`

- 지금까지는 부모 클래스와 자식 클래스를 모두 DB 테이블과 매핑했다면, `@MappedSuperclass`는 `부모 클래스는 테이블과 매핑하지 않고, 자식 클래스에게 매핑 정보만 제공` 할 때 사용된다!

![`@MappedSuperclass`](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%60@MappedSuperclass%60.jpg?raw=true)

위와 같이 공통 속성을 상속하는 Member와 Seller가 있다고 가정해보고 이를 그대로 구현해보자

1. 부모 클래스측에 `@MappedSuperclass` 붙여주기

`BaseEntity.java`

```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@MappedSuperclass  
@NoArgsConstructor  
@Getter  
@ToString  
@TableGenerator(  
        name = "base_entity_generator",  
        pkColumnValue = "base_entity_sequences",  
        allocationSize = 1  
)  
public class BaseEntity {  
    @Id  
 @GeneratedValue(strategy = GenerationType.TABLE,generator = "base_entity_generator")  
    private Long id;  
  
    @Setter  
  private String name;  
}
```

2. 자식 클래스
- Member.java
```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.Entity;  
import javax.persistence.TableGenerator;  
  
@Entity  
@NoArgsConstructor  
@Getter  
@ToString  
public class Member extends BaseEntity{  
    /*  
 BaseEntity 상속 => id, name 상속  
 */  @Setter  
  private String email;  
}
```

- Seller.java

```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.Column;  
import javax.persistence.Entity;  
  
@Entity  
@NoArgsConstructor  
@Getter  
@ToString  
public class Seller extends BaseEntity{  
    /*  
 BaseEntity 상속 => id, name 상속  
 */  @Setter  
 @Column(name = "shop_name")  
    private String shopName;  
}
```

3. 그런데! `부모로부터 물려받은 매핑 정보를 재정의`하려면 `@AttributeOverrides` 혹은 `@AttributeOverride`를 사용하고!
```
@AttributeOverride(name = "컬럼명", column=@Column(name = "자식측에서 변경할 명칭")
```
`연관관계를 재정의`하려면 `@AssociationOverride`나 `@AssociationOverrides`를 사용
➡ 이를 위해서 임의로, `Product`라는 엔티티를 만들고, 이를 이용해보자
https://docs.jboss.org/hibernate/jpa/2.2/api/javax/persistence/AssociationOverride.html 참고!!

일부러, BaseEntity의 @JoinEntity에는 컬럼명을 잘못 표기해두기로 하고 진행해보자

```
@AssociationOverrides({  
        @AssociationOverride(name = "mappedSuperclass(즉, 부모측)에 존재하는 연결 참조 필드명",joinColumns = {@JoinColumn(name = "연결할 반대측의 컬럼명")})  
})
```

✅ 주의할 점은 부모측의 반대편(관계상)에서 OneToMany나 ManyToOne으로 연결해줄 수는 없다(manytoone attribute type should not be Mapped Superclass 표시됨!!)

- BaseEntity.java
```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@MappedSuperclass  
@NoArgsConstructor  
@Getter  
@ToString  
@TableGenerator(  
        name = "base_entity_generator",  
        pkColumnValue = "base_entity_sequences",  
        allocationSize = 1  
)  
public class BaseEntity {  
    @Id  
 @GeneratedValue(strategy = GenerationType.TABLE,generator = "base_entity_generator")  
    private Long id;  
  
    @Setter  
  private String name;  
  
    @ManyToOne  
 @JoinColumn(name = "product")  
    @Setter  
  private Product product;  
}
```

- ProductType.java

```java
package com.example.ch07jpastart4.domain.constant;  
  
public enum ProductType {  
    COMPUTER("컴퓨터"),  
    LIFE("생활용품"),  
    ACCESSORY("악세서리");  
  
    private String type;  
  
    private ProductType(String type){  
        this.type = type;  
    }  
  
    public String getType(){  
        return this.type;  
    }  
}
```

- Product.java
```java
package com.example.ch07jpastart4.domain.entity;  
  
import com.example.ch07jpastart4.domain.constant.ProductType;  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@NoArgsConstructor  
@Getter  
@ToString  
@TableGenerator(  
        name ="product_table_generator",  
        pkColumnValue = "product_sequences",  
        allocationSize = 1  
)  
public class Product {  
    @Id  
 @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_table_generator")  
    @Column(name = "product_id")  
    private Long id;  
  
    @Setter  
  private String name;  
  
    @Setter  
 @Enumerated(EnumType.STRING)  
    private ProductType type;  
  
    @Setter  
  private int price;  
  
    /*  
 Many To One' attribute type should not be 'Mapped Superclass' */
 }
```

- Member.java

```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@NoArgsConstructor  
@Getter  
@ToString  
@AttributeOverrides({  
        @AttributeOverride(name ="id", column = @Column(name = "MEMBER_ID")),  
        @AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME"))  
})  
@AssociationOverrides({  
        @AssociationOverride(name = "product",joinColumns = {@JoinColumn(name = "product_id")})  
})  
public class Member extends BaseEntity{  
    /*  
 BaseEntity 상속 => id, name 상속  
 */  @Setter  
  private String email;  
}
```

- Seller.java

```java
package com.example.ch07jpastart4.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@NoArgsConstructor  
@Getter  
@ToString  
@AttributeOverrides({  
        @AttributeOverride(name ="id", column = @Column(name = "SELLER_ID")),  
        @AttributeOverride(name = "name", column = @Column(name = "SELLER_NAME"))  
})  
@AssociationOverrides({  
        @AssociationOverride(name = "product",joinColumns = {@JoinColumn(name = "product_id")})  
})  
public class Seller extends BaseEntity{  
    /*  
 BaseEntity 상속 => id, name 상속  
 */  @Setter  
 @Column(name = "shop_name")  
    private String shopName;  
}
```

- MappedSuperclassTest.java
```java
package com.example.ch07jpastart4.test;  
  
import com.example.ch07jpastart4.domain.constant.ProductType;  
import com.example.ch07jpastart4.domain.entity.Member;  
import com.example.ch07jpastart4.domain.entity.Product;  
import com.example.ch07jpastart4.domain.entity.Seller;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class MappedSuperclassTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory =  
                Persistence.createEntityManagerFactory("jpabook");  
        EntityManager entityManager =  
                entityManagerFactory.createEntityManager();  
        EntityTransaction tx =  
                entityManager.getTransaction();  
  
        try {  
            tx.begin();  
            logic(entityManager);  
            tx.commit();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            entityManager.close();  
        }  
        entityManagerFactory.close();  
    }  
  
    static void logic(EntityManager entityManager) {  
  
        Product product = new Product();  
        product.setType(ProductType.COMPUTER);  
        product.setName("samsung notebook");  
        product.setPrice(1000000);  
        entityManager.persist(product);  
  
        Member member = new Member();  
        member.setName("member1");  
        member.setEmail("member1@mem.com");  
        member.setProduct(product);  
        entityManager.persist(member);  
  
  
        Seller seller = new Seller();  
        seller.setName("seller1");  
        seller.setShopName("shop1");  
        seller.setProduct(product);  
        entityManager.persist(seller);  
  
        List<Product> products = entityManager.createQuery("select p from Product p", Product.class)  
                .getResultList();  
        List<Member> members = entityManager.createQuery("select m from Member m", Member.class)  
                .getResultList();  
        List<Seller> sellers = entityManager.createQuery("select s from Seller s", Seller.class)  
                .getResultList();  
  
        System.out.println("products: " + products);  
        System.out.println("members: " + members);  
        System.out.println("sellers: " + sellers);  
  
        StringBuilder sb = new StringBuilder();  
        sb.append("members-base entity info: ").append('\n');  
  
        members.forEach(ele1 -> {  
            sb.append("id: ")  
                    .append(ele1.getId())  
                    .append(", ")  
                    .append(ele1.getName())  
                    .append(", ")  
                    .append(ele1.getProduct())  
                    .append('\n');  
        });  
  
        sb.append("sellers-base entity info: ").append('\n');  
  
        sellers.forEach(ele1 -> {  
            sb.append("id: ")  
                    .append(ele1.getId())  
                    .append(", ")  
                    .append(ele1.getName())  
                    .append(", ")  
                    .append(ele1.getProduct())  
                    .append('\n');  
        });  
  
        System.out.print(sb);  
    }  
}
```

```
Hibernate: 
    
    create table Member (
       MEMBER_ID bigint not null,
        MEMBER_NAME varchar(255),
        email varchar(255),
        product_id bigint,
        primary key (MEMBER_ID)
    )
Hibernate: 
    
    create table Product (
       product_id bigint not null,
        name varchar(255),
        price integer not null,
        type varchar(255),
        primary key (product_id)
    )
Hibernate: 
    
    create table Seller (
       SELLER_ID bigint not null,
        SELLER_NAME varchar(255),
        shop_name varchar(255),
        product_id bigint,
        primary key (SELLER_ID)
    )
Hibernate: 
    
    alter table Member 
       add constraint FK21msvnowrbuw8d99ujg1f4ei3 
       foreign key (product_id) 
       references Product
Hibernate: 
    
    alter table Seller 
       add constraint FKfjf2enm3dqfd7irjbuhahrylp 
       foreign key (product_id) 
       references Product
Hibernate: 
    insert 
    into
        Product
        (name, price, type, product_id) 
    values
        (?, ?, ?, ?)
Hibernate: 
    insert 
    into
        Member
        (MEMBER_NAME, product_id, email, MEMBER_ID) 
    values
        (?, ?, ?, ?)
Hibernate: 
    insert 
    into
        Seller
        (SELLER_NAME, product_id, shop_name, SELLER_ID) 
    values
        (?, ?, ?, ?)
Hibernate: 
    select
        product0_.product_id as product_1_1_,
        product0_.name as name2_1_,
        product0_.price as price3_1_,
        product0_.type as type4_1_ 
    from
        Product product0_
Hibernate: 
    select
        member0_.MEMBER_ID as member_i1_0_,
        member0_.MEMBER_NAME as member_n2_0_,
        member0_.product_id as product_4_0_,
        member0_.email as email3_0_ 
    from
        Member member0_
Hibernate: 
    select
        seller0_.SELLER_ID as seller_i1_2_,
        seller0_.SELLER_NAME as seller_n2_2_,
        seller0_.product_id as product_4_2_,
        seller0_.shop_name as shop_nam3_2_ 
    from
        Seller seller0_
22:07:20.720 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart4.domain.entity.Seller#1]
products: [Product(id=1, name=samsung notebook, type=COMPUTER, price=1000000)]
members: [Member(email=member1@mem.com)]
sellers: [Seller(shopName=shop1)]
members-base entity info: 
id: 1, member1, Product(id=1, name=samsung notebook, type=COMPUTER, price=1000000)
sellers-base entity info: 
id: 1, seller1, Product(id=1, name=samsung notebook, type=COMPUTER, price=1000000)
```

그러면 위와 같이 Member, Seller, Product 테이블이 생기는데, BaseEntity는 공통속성을 다루는 부모 클래스 개념으로만 존재하고, 이 객체의 속성이나 연관관계를 물려받되, db상 매칭이 정확히 이루어지지 않아도 됨을 확인해볼 수 있다

✅ `@MappedSuperclass의 특징`

- 테이블과 매핑되지 않고 자식 클래스에 엔티티의 매핑정보를 상속하기 위해 사용
- `@MappedSuperclass로 지정한 클래스는 엔티티가 아니므로 em.find()나 JPAL에서 사용 불가`
- 이 클래스를 `직접 생성해서 사용할 일은 거의 없기 때문에 추상 클래스로 만드는 것이 권장`됨!!

이 방식은 등록일자, 수정일자, 등록자, 수정자와 같은 공통적으로 사용될 수 있는 속성을 보다 효율적으로 관리할 수 있지만, `ORM에서 언급하는 진정한 상속 매핑은 앞에서 언급되었던 슈퍼타입 서브타입 관계와 매핑하는 것`이다!!

💥 `엔티티`가 상속받을 수 있는 대상

- 엔티티
- `@MappedSuperclass로 지정한 클래스`

## 3. 복합키와 식별관계 매핑

### 3-1. 식별관계 vs 비식별관계

- 식별관계 `Identifying Relationship` : 자식 테이블에서  `부모테이블의 PK가 FK로써도, PK로써도 사용됨`

![식별관계](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EB%B3%B5%ED%95%A9%ED%82%A4%EC%99%80_%EC%8B%9D%EB%B3%84%EA%B4%80%EA%B3%84_%EB%A7%A4%ED%95%91/%EC%8B%9D%EB%B3%84%EA%B4%80%EA%B3%84.jpg?raw=true)

- 비식별관계 `Non-Identifying Relationship` : 자식 테이블에 `부모 테이블의 PK는 FK로써만 존재`
![비식별관계](https://github.com/hy6219/JPA_QueryDSL/blob/main/inheritance_mapping/%EB%B3%B5%ED%95%A9%ED%82%A4%EC%99%80_%EC%8B%9D%EB%B3%84%EA%B4%80%EA%B3%84_%EB%A7%A4%ED%95%91/%EB%B9%84%EC%8B%9D%EB%B3%84%EA%B4%80%EA%B3%84.jpg?raw=true)

- 비식별관계는 `FK에 NULL을 허용하는 지 여부에 따라` `필수적 비식별 관계`, `선택적 비식별 관계`로 분류

1️⃣ `필수적 비식별 관계` (mandatory)

- 외래키에 NULL 허용 x
- 연관관계를 필수적으로 맺어야 함

2️⃣ `선택적 비식별 관계`(optional)

- 외래키에 NULL 허용
- 연관관계를 맺을 지 말지 선택 가능


- jpa는 필수적, 선택적 비식별 관계를 모두 지원!

### 3-2. 복합 키 : 비식별 관계 매핑

- jpa : 영속성 컨텍스트에 엔티티 보관시 엔티티의 식별자를 키로 사용하고, 이 식별자를 구분하기 위해서 equals & hashCode를 사용해서 동등성 비교 진행

✅ `복합키 지원`

- `@IdClass` : RDBMS에 가까운 방법
- `@EmbeddedId` : 좀 더 객체지향에 가까운 방법

#### 3-2-1. `@IdClass` 이용

1️⃣ `Serializable을 구현한 복합키들만 존재하는 복합키 클래스` 준비

- 단, **이때 복합키 필드명은 자식 클래스 필드명과 맞춰주어야 함**💥💥💥
- equals, hashCode 구현
- `기본 생성자`가 있어야 함
- `식별자 클래스는 public`
- `Serializable` 구현
```java
package com.example.ch07jpastart5.domain.entity;  
  
import lombok.*;  
  
import java.io.Serializable;  
  
@NoArgsConstructor  
@AllArgsConstructor  
@EqualsAndHashCode  
@Getter  
@Setter  
public class ParentComplexId implements Serializable {  
    private String id1;//자식클래스 필드명과 맞춰주어야 함  
  private String id2;  
}
```

2️⃣ `1️⃣ 을 PK로 사용하는 Parent 클래스 엔티티 준비`

```java
package com.example.ch07jpastart5.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.Column;  
import javax.persistence.Entity;  
import javax.persistence.Id;  
import javax.persistence.IdClass;  
  
@Entity  
@IdClass(value = ParentComplexId.class)  
@NoArgsConstructor  
@Getter  
@Setter  
@ToString  
public class Parent {  
    /**  
 * 복합키클래스에서의 필드명과 맞춰주어야 함  
  */  
  @Id  
 @Column(name ="PARENT_ID1")  
    private String id1;  
  
  @Id  
 @Column(name = "PARENT_ID2")  
    private String id2;  
  
  @Column(name = "NAME")  
    private String name;  
}
```
간단하게 Parent 엔티티에 복합키를 적용하는 과정을 확인해보자

```java
package com.example.ch07jpastart5.test;  
  
import com.example.ch07jpastart5.domain.entity.Parent;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class ParentComplexIdTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");  
  EntityManager entityManager = entityManagerFactory.createEntityManager();  
  EntityTransaction tx = entityManager.getTransaction();  
  
 try {  
            tx.begin();  
  logic(entityManager);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            entityManager.close();  
  }  
        entityManagerFactory.close();  
  }  
  
    static void logic(EntityManager entityManager){  
        Parent parent = new Parent();  
  parent.setId1("parent_id1");  
  parent.setId2("parent_id2");  
  parent.setName("name1");  
  entityManager.persist(parent);  
  
  List<Parent> saved = entityManager.createQuery("select p from Parent p",Parent.class)  
                .getResultList();  
  
  System.out.println("저장되었던 모든 Parents: "+saved);  
  }  
}
```

```
09:52:04.679 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Parent
        (NAME, PARENT_ID1, PARENT_ID2) 
    values
        (?, ?, ?)
Hibernate: 
    insert 
    into
        Parent
        (NAME, PARENT_ID1, PARENT_ID2) 
    values
        (?, ?, ?)
09:52:04.691 [main] DEBUG org.hibernate.SQL - 
    select
        parent0_.PARENT_ID1 as parent_i1_0_,
        parent0_.PARENT_ID2 as parent_i2_0_,
        parent0_.NAME as name3_0_ 
    from
        Parent parent0_
Hibernate: 
    select
        parent0_.PARENT_ID1 as parent_i1_0_,
        parent0_.PARENT_ID2 as parent_i2_0_,
        parent0_.NAME as name3_0_ 
    from
        Parent parent0_
09:52:04.694 [main] DEBUG org.hibernate.loader.Loader - Result row: EntityKey[com.example.ch07jpastart5.domain.entity.Parent#component[id1,id2]{id2=parent_id2, id1=parent_id1}]
저장되었던 모든 Parents: [Parent(id1=parent_id1, id2=parent_id2, name=name1)]
```

그런데 처리 결과를 보면, 식별자 클래스인 ParentComplexId가 보이지 않는다. 이는 entityManager.persist 전에 내부에서 Parent.id1, Parent.id2 값을 사용해서 ParentComplexId를 생성하고 영속성 컨텍스트의 키로 사용되기 때문이다

ParentComplexId로 조회해보자

```java
package com.example.ch07jpastart5.test;  
  
import com.example.ch07jpastart5.domain.entity.Parent;  
import com.example.ch07jpastart5.domain.entity.ParentComplexId;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
import java.util.List;  
  
public class ParentComplexIdTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");  
  EntityManager entityManager = entityManagerFactory.createEntityManager();  
  EntityTransaction tx = entityManager.getTransaction();  
  
 try {  
            tx.begin();  
  logic(entityManager);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            entityManager.close();  
  }  
        entityManagerFactory.close();  
  }  
  
    static void logic(EntityManager entityManager){  
        Parent parent = new Parent();  
  parent.setId1("parent_id1");  
  parent.setId2("parent_id2");  
  parent.setName("name1");  
  entityManager.persist(parent);  
  
  List<Parent> saved = entityManager.createQuery("select p from Parent p",Parent.class)  
                .getResultList();  
  
  System.out.println("저장되었던 모든 Parents: "+saved);  
  
  //ParentComplexId로 조회  
  ParentComplexId complexId = new ParentComplexId();  
  complexId.setId1("parent_id1");  
  complexId.setId2("parent_id2");  
  Parent findByComplexId = entityManager.find(Parent.class,complexId);  
  System.out.println("ParentComplexId로 조회: "+findByComplexId);  
  }  
}
```
```
저장되었던 모든 Parents: [Parent(id1=parent_id1, id2=parent_id2, name=name1)]
ParentComplexId로 조회: Parent(id1=parent_id1, id2=parent_id2, name=name1)
```

확인해본 결과, ParentComplexId로 Parent를 조회할 수 있음을 확인해볼 수 있다

이제 자식클래스를 추가해보자

3️⃣ 부모 테이블의 기본키 컬럼이 복합키이기 때문에, `연관관계 매핑하는 부분에서 @JoinColumns({@JoinColumn})으로 어떤 컬럼을 참조(referencedColumnName)할 것이고, 자식 클래스에서는 이름(name)을 무엇으로 할 것인지` 명시해주자!
만약, name과 referencedColumnName을 같게 한다면, referencedColumnName은 생략해도 된다!!

↔ 
```java
package com.example.ch07jpastart5.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@Getter  
@Setter  
@NoArgsConstructor  
@ToString  
public class Child {  
    @Id  
  private String id;  
  
  @ManyToOne  
 @JoinColumns({  
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),  
  @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")  
    })  
    private Parent parent;  
}
```
↔
```java
package com.example.ch07jpastart5.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@Getter  
@Setter  
@NoArgsConstructor  
@ToString  
public class Child {  
    @Id  
  private String id;  
  
  @ManyToOne  
 @JoinColumns({  
            @JoinColumn(name = "PARENT_ID1"),  
  @JoinColumn(name = "PARENT_ID2")  
    })  
    private Parent parent;  
}
```
DDL을 살펴보면 아래처럼 Parent 테이블은 복합키가 생성되고, Child는 alter로 FK로써 그 복합키를 참조하는 것을 확인해볼 수 있다

```
create table Child (
       id varchar(255) not null,
        PARENT_ID1 varchar(255),
        PARENT_ID2 varchar(255),
        primary key (id)
    )
create table Parent (
       PARENT_ID1 varchar(255) not null,
        PARENT_ID2 varchar(255) not null,
        NAME varchar(255),
        primary key (PARENT_ID1, PARENT_ID2)
    )
alter table Child 
       add constraint FKiw6nxs5a8k6vrivlfrb62qp8q 
       foreign key (PARENT_ID1, PARENT_ID2) 
       references Parent
```

간단하게 자식 클래스도 테스트해보자
```java
package com.example.ch07jpastart5.test;  
  
import com.example.ch07jpastart5.domain.entity.Child;  
import com.example.ch07jpastart5.domain.entity.Parent;  
import com.example.ch07jpastart5.domain.entity.ParentComplexId;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
  
public class ChildTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");  
  EntityManager entityManager = entityManagerFactory.createEntityManager();  
  EntityTransaction tx = entityManager.getTransaction();  
  
 try {  
            tx.begin();  
  logic(entityManager);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            entityManager.close();  
  }  
        entityManagerFactory.close();  
  }  
  
    static void logic(EntityManager entityManager){  
        Parent parent = new Parent();  
  parent.setId1("id1");  
  parent.setId2("id2");  
  parent.setName("namename");  
  entityManager.persist(parent);  
  
  Child child = new Child();  
  child.setId("child");  
  child.setParent(parent);  
  entityManager.persist(child);  
  
  Child find = entityManager.find(Child.class,"child");  
  System.out.println("find child: "+find);  
  System.out.println("parent: "+find.getParent());  
  }  
}
```
```
10:16:01.384 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Parent
        (NAME, PARENT_ID1, PARENT_ID2) 
    values
        (?, ?, ?)
Hibernate: 
    insert 
    into
        Parent
        (NAME, PARENT_ID1, PARENT_ID2) 
    values
        (?, ?, ?)
10:16:01.392 [main] DEBUG org.hibernate.SQL - 
    insert 
    into
        Child
        (PARENT_ID1, PARENT_ID2, id) 
    values
        (?, ?, ?)
Hibernate: 
    insert 
    into
        Child
        (PARENT_ID1, PARENT_ID2, id) 
    values
        (?, ?, ?)

10:16:01.333 [main] DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: child, using strategy: org.hibernate.id.Assigned
find child: Child(id=child, parent=Parent(id1=id1, id2=id2, name=namename))
parent: Parent(id1=id1, id2=id2, name=namename)
```
그러면 위와 같이 Parent, Child가 모두 확인되는 모습을 볼 수 있다

#### 3-2-2. `@EmbeddedId` 이용

✅ `@IdClass` vs `@EmbeddedId`

-  `@IdClass`: db에 맞춘 방법
- `@EmbeddedId` : 좀 더 객체지향적인 방법, 중복도 없어서 좋아보이기는 하지만 특정 상황에 JPQL이 조금 더 길어보일 수 있음

ex) p.261
```
//1.`@EmbeddedId`
em.createQuery("select p.id.id1,p.id.id2 from Parent p",Parent.class)
//2.`@IdClass`
em.createQuery("select p.id1,p.id2 from Parent p", Parent.class)
```

-------

💥💥💥 `@EmbeddedId`  를 적용한 식별자 클래스가 만족해야하는 조건

- `@Embeddable` 어노테이션을 `복합키 클래스`에 붙여주기
- `Serializable` 인터페이스를 복합키 클래스에서 구현해주기
- `equals, hashCode를 구현`해야 함
- `기본 생성자`가 있어야 함
- `식별자 클래스는 public` 이어야 함

1️⃣ 식별자 클래스를 만들어보자

```java
package com.example.ch07jpastart6.domain.entity;  
  
import lombok.EqualsAndHashCode;  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
  
import javax.persistence.Column;  
import javax.persistence.Embeddable;  
import java.io.Serializable;  
  
@Embeddable  
@Getter  
@Setter  
@EqualsAndHashCode  
@NoArgsConstructor  
public class ParentId implements Serializable {  
    @Column(name = "PARENT_ID1")  
    private String id1;  
  
  @Column(name = "PARENT_ID2")  
    private String id2;  
}
```

2️⃣ 복합키를 사용할 Parent 클래스에서 `@EmbeddedId`를 달아주자
```java
@EmbeddedId
private ParentId id;
```
```java
package com.example.ch07jpastart6.domain.entity;  
  
import lombok.Getter;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.Column;  
import javax.persistence.EmbeddedId;  
import javax.persistence.Entity;  
  
@Entity  
@Getter  
@Setter  
@ToString  
public class Parent {  
    @EmbeddedId  
  private ParentId id;  
  
  @Column(name = "name")  
    private String name;  
}
```

```java
package com.example.ch07jpastart6.test;  
  
import com.example.ch07jpastart6.domain.entity.Parent;  
import com.example.ch07jpastart6.domain.entity.ParentId;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
  
public class EmbeddedIdParentTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");  
  EntityManager entityManager = entityManagerFactory.createEntityManager();  
  EntityTransaction tx = entityManager.getTransaction();  
  
 try {  
            tx.begin();  
  logic(entityManager);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            entityManager.close();  
  }  
        entityManagerFactory.close();  
  }  
  
    static void logic(EntityManager entityManager){  
        ParentId id = new ParentId();  
  id.setId1("id1");  
  id.setId2("id2");  
  
  Parent parent = new Parent();  
  parent.setId(id);  
  parent.setName("name1");  
  entityManager.persist(parent);  
  
  Parent find = entityManager.find(Parent.class,id);  
  System.out.println("find: "+find);  
  }  
}
```

```
create table Parent (
       PARENT_ID1 varchar(255) not null,
        PARENT_ID2 varchar(255) not null,
        name varchar(255),
        primary key (PARENT_ID1, PARENT_ID2)
    )

find: Parent(id=com.example.ch07jpastart6.domain.entity.ParentId@5f5142, name=name1)    
```

3️⃣ 이번에는 비식별 관계에 있는 자식클래스를 추가해보자

```java
package com.example.ch07jpastart6.domain.entity;  
  
import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  
  
import javax.persistence.*;  
  
@Entity  
@Getter  
@Setter  
@ToString  
@NoArgsConstructor  
public class Child {  
    @Id  
  private String id;  
  
  @ManyToOne  
 @JoinColumns({  
            @JoinColumn(name = "PARENT_ID1"),  
  @JoinColumn(name = "PARENT_ID2")  
    })  
    private Parent parent;  
}
```

```java
package com.example.ch07jpastart6.test;  
  
import com.example.ch07jpastart6.domain.entity.Child;  
import com.example.ch07jpastart6.domain.entity.Parent;  
import com.example.ch07jpastart6.domain.entity.ParentId;  
  
import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.EntityTransaction;  
import javax.persistence.Persistence;  
  
public class ChildTest {  
    public static void main(String[] args) {  
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");  
  EntityManager entityManager = entityManagerFactory.createEntityManager();  
  EntityTransaction tx = entityManager.getTransaction();  
  
 try {  
            tx.begin();  
  logic(entityManager);  
  tx.commit();  
  }catch (Exception e){  
            e.printStackTrace();  
  }finally {  
            entityManager.close();  
  }  
  
        entityManagerFactory.close();  
  }  
  
    static void logic(EntityManager entityManager){  
        ParentId id = new ParentId();  
  id.setId1("idid1");  
  id.setId2("idid2");  
  
  Parent parent = new Parent();  
  parent.setId(id);  
  parent.setName("p");  
  entityManager.persist(parent);  
  
  Child child = new Child();  
  child.setId("childchild");
  child.setParent(parent);  
  entityManager.persist(child);  
  
  Parent findParent = entityManager.find(Parent.class,id);  
  Child findChild = entityManager.find(Child.class,"childchild");  
  System.out.println("findParent: "+findParent);  
  System.out.println("findChild: "+findChild);  
  }  
}
```
```
 create table Child (
       id varchar(255) not null,
        PARENT_ID1 varchar(255),
        PARENT_ID2 varchar(255),
        primary key (id)
    )
create table Parent (
       PARENT_ID1 varchar(255) not null,
        PARENT_ID2 varchar(255) not null,
        name varchar(255),
        primary key (PARENT_ID1, PARENT_ID2)
    )
 alter table Child 
       add constraint FKiw6nxs5a8k6vrivlfrb62qp8q 
       foreign key (PARENT_ID1, PARENT_ID2) 
       references Parent
11:44:43.790 [main] DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: childchild, using strategy: org.hibernate.id.Assigned
findParent: Parent(id=com.example.ch07jpastart6.domain.entity.ParentId@65d12e6e, name=p)
findChild: Child(id=childchild, parent=Parent(id=com.example.ch07jpastart6.domain.entity.ParentId@65d12e6e, name=p))
```
그러면 역시, 결과는 동일 패턴으로 확인될 수 있음을 알 수 있다.
다만, `ParentId-Parent` 간의 작업을 비교해보면, 

- `@IdClass`는 복합키 클래스의 필드명을 그대로 맞춰서 적어주어야 했지만
- `@EmbeddedId`는 복합키 클래스를 인스턴스로 두기만 하면 물려받을 수 있다

✅ 복합키에서 조심할 점

- equals, hashCode를 구현해서 동등성을 확인해서 같은 엔티티인지 확인이 필요
- `@GeneratedValue`를 사용할 수 없음

### 3-3. 복합 키 : 식별 관계 매핑


