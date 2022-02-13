# `Multiple persistence-unit in persistence.xml`

갑자기 persistence.xml에서 persistence-unit이 2개 이상이면 어떨까? 라는 생각이 들어서 찾아보았다

https://stackoverflow.com/questions/5356152/two-persistence-unit-in-one-persistence-xml

방법은 2가지!!

방법 1️⃣ `@PersistenceUnit(name = "persistence-unit 명")`-EntityManagerFactory, `@PersistenceContext(unitName="persistence-unit 명")` - EntityManager 사용

방법 2️⃣ 수동으로 주입없이 아래처럼 사용


```java
EntityManagerFactory emfA = Persistence.createEntityManagerFactory("x", properties);
EntityManagerFactory emfB = Persistence.createEntityManagerFactory("y", properties);
```
