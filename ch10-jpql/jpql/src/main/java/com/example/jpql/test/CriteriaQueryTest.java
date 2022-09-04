package com.example.jpql.test;

import com.example.jpql.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CriteriaQueryTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        //1. Criteria 사용 준비
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        //2. 루트 클래스(조회를 시작할 클래스)를 만들기
        Root<Member> m = query.from(Member.class);

        //3. 쿼리 생성
        //현재 엔티티에서 name 컬럼을 "username"으로 받고 있고, name 속성의 값이 x인 경우를 조회할 것이므로 아래와 같이 될 것
        CriteriaQuery<Member> where = query.select(m).where(cb.equal(m.get("username"), "테스트1"));
        List<Member> resultList = em.createQuery(where).getResultList();
        System.out.println("result: " + resultList);
    }
}
