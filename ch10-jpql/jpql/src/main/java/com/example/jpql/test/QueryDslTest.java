package com.example.jpql.test;

import com.example.jpql.domain.Member;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class QueryDslTest {
    public static void main(String[] args) {
        //1. 준비
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        JPAQuery<Member> query = new JPAQuery<>(em);

    }
}
