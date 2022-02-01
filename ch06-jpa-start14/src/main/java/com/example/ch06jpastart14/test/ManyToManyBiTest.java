package com.example.ch06jpastart14.test;

import com.example.ch06jpastart14.domain.entity.Member;
import com.example.ch06jpastart14.domain.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ManyToManyBiTest {
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
        Product productA = new Product();
        productA.setPrice(1000);
        productA.setName("새우깡");
        entityManager.persist(productA);

        Product productB = new Product();
        productB.setPrice(1000);
        productB.setName("옥수수깡");
        entityManager.persist(productB);

        Member member1 = new Member();
        member1.setUsername("농심");
        member1.setProducts(List.of(productA,productB));
        entityManager.persist(member1);

        //양방향
        productA.setMembers(List.of(member1));
        productB.setMembers(List.of(member1));
    }

    static void search(EntityManager entityManager){
        Member findMember = entityManager.find(Member.class,1L);
        List<Product> ref = findMember.getProducts();//객체 그래프 탐색

        ref.forEach(product -> {
            System.out.println("member로 접근한 products: "+product);
        });

        Product findProduct = entityManager.find(Product.class,1L);
        List<Member> members = findProduct.getMembers();

        members.forEach(member->{
            System.out.println("product로 접근한 members: "+member);
        });
    }
}
