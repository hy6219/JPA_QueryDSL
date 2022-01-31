package com.example.ch06jpastart11.test;

import com.example.ch06jpastart11.domain.entity.Locker;
import com.example.ch06jpastart11.domain.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class OneToOneMainBiTest {
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

        //양방향이므로 양측에서 관계 설정
        locker.setMember(member);
    }

    static void search(EntityManager entityManager){
        Locker findLocker = entityManager.find(Locker.class,1L);
        Member findMember = entityManager.find(Member.class,1L);

        System.out.println("Locker로 Member 정보 확인: [Locker: "+findLocker+"], member by locker: ["+findLocker.getMember()+"]");
        System.out.println("Member로 Locker 정보 확인: [Member: "+findMember+"], locker by member: ["+findMember.getLocker()+"]");
    }
}
