package com.example.ch04jpastart2.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class AutoStrategyTest {
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        //3.EntityTransaction 객체
        EntityTransaction tx =
                entityManager.getTransaction();

        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.로직
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager){

        Food food = new Food();
        food.setName("떡볶이");

        //영속성 컨텍스트에 등록
        entityManager.persist(food);

        //영속성 컨텍스트에 등록된 모든 Food 엔티티들을 조회
        List<Food> foods =
                entityManager.createQuery("select food from Food food",Food.class)
                        .getResultList();

        System.out.println("등록된 모든 Food 객체들: "+foods);
    }
}
