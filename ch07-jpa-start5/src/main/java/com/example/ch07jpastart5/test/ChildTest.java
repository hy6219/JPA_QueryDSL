package com.example.ch07jpastart5.test;

import com.example.ch07jpastart5.domain.entity.Child;
import com.example.ch07jpastart5.domain.entity.Parent;
import com.example.ch07jpastart5.domain.entity.ParentComplexId;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ChildTest {
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
        parent.setId1("id1");
        parent.setId2("id2");
        parent.setName("namename");
        entityManager.persist(parent);

        Child child = new Child();
        child.setId("child");
        child.setParent(parent);
        entityManager.persist(child);

        Child find = entityManager.find(Child.class,"child");
        System.out.println("find child: "+find);
        System.out.println("parent: "+find.getParent());
    }
}
