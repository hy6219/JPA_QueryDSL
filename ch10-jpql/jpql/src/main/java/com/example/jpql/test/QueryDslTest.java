package com.example.jpql.test;

import com.example.jpql.domain.Member;
import com.example.jpql.domain.QMember;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class QueryDslTest {
    public static void main(String[] args) {
        //1. 준비
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        JPAQuery<Member> query = new JPAQuery<>(em);
        QMember qMember = QMember.member;

        //2. 쿼리, 결과 조회
        List<Member> members = query.from(qMember)
                .where(qMember.username.eq("테스트1"))
                .fetch();

        System.out.println("results : " + members);
    }
}
