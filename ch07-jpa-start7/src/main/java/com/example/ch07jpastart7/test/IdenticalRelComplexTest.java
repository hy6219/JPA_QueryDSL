package com.example.ch07jpastart7.test;

import com.example.ch07jpastart7.domain.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class IdenticalRelComplexTest {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();
            logic(entityManager);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager) {
        Parent parent = new Parent();
        parent.setId("p1");
        parent.setName("parent1");
        entityManager.persist(parent);

        //복합키로 조회할 때 테스트용 목적
        ChildId childId = new ChildId();
        childId.setParent(parent.getId());
        childId.setChildId("child1");

        Child child = new Child();
        child.setChildId("child1");
        child.setParent(parent);
        child.setName("childchild");
        entityManager.persist(child);

        //복합키로 조회할 때 테스트용 목적
        GrandChildId grandChildId = new GrandChildId();
        grandChildId.setChild(childId);
        grandChildId.setGrandChildId("grandgrand");

        GrandChild grandChild = new GrandChild();
        grandChild.setChild(child);
        grandChild.setGrandChildId("grandgrand");
        grandChild.setName("grandName");
        entityManager.persist(grandChild);

        //조회
        Parent findParent = entityManager.find(Parent.class,"p1");
        Child findChild = entityManager.find(Child.class,childId);
        GrandChild findGrand = entityManager.find(GrandChild.class,grandChildId);

        System.out.println("findParent: "+findParent);
        System.out.println("findChild: "+findChild);
        System.out.println("findGrand: "+findGrand);
    }
}
