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
