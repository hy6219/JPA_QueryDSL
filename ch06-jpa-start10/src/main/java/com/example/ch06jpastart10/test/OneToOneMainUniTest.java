package com.example.ch06jpastart10.test;

import com.example.ch06jpastart10.domain.entity.Locker;
import com.example.ch06jpastart10.domain.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author gs813
 * 일대일 주테이블에 외래키가 있는 경우-단방향
 */
public class OneToOneMainUniTest {
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
        save(entityManager);
        search(entityManager);
    }

    static void save(EntityManager entityManager){
        Locker locker = new Locker();
        locker.setName("locker1");
        entityManager.persist(locker);

        Member member = new Member();
        member.setUsername("member1");
        member.setLocker(locker);
        entityManager.persist(member);
    }

    static void search(EntityManager entityManager){
        Member findMember = entityManager.find(Member.class,1L);
        System.out.println("find 1L member: "+findMember);
        System.out.println("find locker by member: "+findMember.getLocker());
    }
}
