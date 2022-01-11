package com.example.ch04jpastart2.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class TableStrategyTest {
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
            //6.트랜잭션 종료
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            entityManager.close();
        }

        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager){
        Article article = new Article();
        article.setWriter("가나다");
        article.setContent("테스트1");

        //영속성 컨텍스트 관리를 위한 등록
        entityManager.persist(article);

        //영속성 컨텍스트가 관리하는 모든 Article 객체들을 조회
        List<Article> articles =
                entityManager.createQuery("select a from Article  a",Article.class)
                        .getResultList();

        System.out.println("현재 영속성 컨텍스트가 관리하는 모든 article 객체들 : "+articles);
    }
}
