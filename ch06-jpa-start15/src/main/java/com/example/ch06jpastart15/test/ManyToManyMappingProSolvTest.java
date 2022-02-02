package com.example.ch06jpastart15.test;

import com.example.ch06jpastart15.domain.entity.Member;
import com.example.ch06jpastart15.domain.entity.MemberProduct;
import com.example.ch06jpastart15.domain.entity.MemberProductId;
import com.example.ch06jpastart15.domain.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ManyToManyMappingProSolvTest {
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
        //회원 저장
        Member member = new Member();
        member.setUsername("member1");
        entityManager.persist(member);

        //상품 저장
        Product product = new Product();
        product.setName("product1");
        entityManager.persist(product);

        //회원상품 저장
        MemberProduct memberProduct = new MemberProduct();
        memberProduct.setMember(member);
        memberProduct.setProduct(product);
        memberProduct.setOrderAmount(3);
        entityManager.persist(memberProduct);
    }

    static void search(EntityManager entityManager){
        MemberProductId complexId = new MemberProductId();
        complexId.setMember(1L);
        complexId.setProduct(1L);

        MemberProduct find = entityManager.find(MemberProduct.class,complexId);
        Member member = find.getMember();
        Product product = find.getProduct();

        System.out.println("연결 엔티티로부터 접근한 member: "+member);
        System.out.println("연결 엔티티로부터 접근한 product: "+product);
    }
}
