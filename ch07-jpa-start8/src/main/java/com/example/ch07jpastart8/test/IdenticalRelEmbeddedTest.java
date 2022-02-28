package com.example.ch07jpastart8.test;

import com.example.ch07jpastart8.domain.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class IdenticalRelEmbeddedTest {
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
        parent.setId("p2");
        parent.setName("p2");
        entityManager.persist(parent);

        ChildId childId = new ChildId();
        childId.setParentId(parent.getId());
        childId.setId("child1");

        Child child = new Child();
        child.setId(childId);
        child.setParent(parent);
        child.setName("childchild");
        entityManager.persist(child);

        GrandChildId grandChildId = new GrandChildId();
        grandChildId.setId("grandChildId");
        grandChildId.setChildId(childId);

        GrandChild grandChild = new GrandChild();
        grandChild.setId(grandChildId);
        grandChild.setChild(child);
        grandChild.setName("grandgrandgrand");
        entityManager.persist(grandChild);

        //조회

    }
}
