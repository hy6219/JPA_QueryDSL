package com.example.jpql.test;

import com.example.jpql.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class MemberTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Member member = new Member();
            member.setUsername("테스트1");

            em.persist(member);

            String jpql = "select m from Member as m where m.username = '테스트1'";
            List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();

            System.out.println("result : " + resultList);

            tx.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

    }
}
