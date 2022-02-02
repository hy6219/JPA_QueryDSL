package com.example.ch06jpastart16.test;

import com.example.ch06jpastart16.domain.entity.Member;
import com.example.ch06jpastart16.domain.entity.OrderTable;
import com.example.ch06jpastart16.domain.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * 새로운 기본키로 다대다 한계 극복
 */
public class ManyToManyUnidentifiableProSolvTest {
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
        //상품저장
        Product product1 = new Product();
        product1.setName("product1");
        entityManager.persist(product1);

        //회원저장
        Member member1 = new Member();
        member1.setUsername("member1");
        entityManager.persist(member1);

        //주문저장
        OrderTable order = new OrderTable();
        order.setOrderAmount(2);
        order.setProduct(product1);
        order.setMember(member1);//양방향 적용
        entityManager.persist(order);
    }

    static void search(EntityManager entityManager){
        OrderTable order = entityManager.find(OrderTable.class,1L);
        Member member = order.getMember();
        Product product = order.getProduct();

        System.out.println("order로 접근한 member: "+member);
        System.out.println("order로 접근한 product: "+product);
    }
}
