package com.example.jpql.test;

import com.example.jpql.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class NativeSqlTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String sql = "select memberId, name from MEMBER where name = '테스트1'";
        List<Member> members = em.createNativeQuery(sql, Member.class).getResultList();
        System.out.println("results : " + members);

    }
}
