package com.example.ch07jpastart11.test;

import com.example.ch07jpastart11.domain.entity.Child;
import com.example.ch07jpastart11.domain.entity.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 일대일조인테이블테스트 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

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
        Parent parent = new Parent();
        parent.setName("parent");
        entityManager.persist(parent);

        Child child = new Child();
        child.setName("child");
        entityManager.persist(child);

        parent.setChild(child);

        Parent find = entityManager.find(Parent.class,1L);
        System.out.println("parent: "+find);
        System.out.println("child by parent: "+find.getChild());
    }
}
