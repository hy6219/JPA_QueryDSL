package com.example.ch06jpastart12.test;

import com.example.ch06jpastart12.domain.entity.Locker;
import com.example.ch06jpastart12.domain.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author gs813
 * 대상테이블에 외래키가 있는 경우 양방향
 * - 연관관계주인을 대상테이블로 하고 양방향으로 생각
 */
public class OneToOneAnotherSideBiTest {
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
        save(entityManager);
        search(entityManager);
    }

    static void save(EntityManager entityManagger) {
        Locker locker = new Locker();
        locker.setName("locker");
        entityManagger.persist(locker);

        Member member = new Member();
        member.setUsername("member");
        member.setLocker(locker);
        entityManagger.persist(member);

        locker.setMember(member);
    }

    static void search(EntityManager entityManager) {
        Locker findLocker = entityManager.find(Locker.class, 1L);
        Member findMember = entityManager.find(Member.class, 1L);

        System.out.println("Locker로 Member 정보 확인: [Locker: " + findLocker + "], member by locker: [" + findLocker.getMember() + "]");
        System.out.println("Member로 Locker 정보 확인: [Member: " + findMember + "], locker by member: [" + findMember.getLocker() + "]");
    }
}
