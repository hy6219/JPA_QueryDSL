package com.example.ch07jpastart6.test;

import com.example.ch07jpastart6.domain.entity.Child;
import com.example.ch07jpastart6.domain.entity.Parent;
import com.example.ch07jpastart6.domain.entity.ParentId;

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
        ParentId id = new ParentId();
        id.setId1("idid1");
        id.setId2("idid2");

        Parent parent = new Parent();
        parent.setId(id);
        parent.setName("p");
        entityManager.persist(parent);

        Child child = new Child();
        child.setId("childchild");
        child.setParent(parent);
        entityManager.persist(child);

        Parent findParent = entityManager.find(Parent.class,id);
        Child findChild = entityManager.find(Child.class,"childchild");
        System.out.println("findParent: "+findParent);
        System.out.println("findChild: "+findChild);
    }
}
