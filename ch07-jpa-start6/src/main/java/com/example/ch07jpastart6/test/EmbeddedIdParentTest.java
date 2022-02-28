package com.example.ch07jpastart6.test;

import com.example.ch07jpastart6.domain.entity.Parent;
import com.example.ch07jpastart6.domain.entity.ParentId;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EmbeddedIdParentTest {
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
        id.setId1("id1");
        id.setId2("id2");

        Parent parent = new Parent();
        parent.setId(id);
        parent.setName("name1");
        entityManager.persist(parent);

        Parent find = entityManager.find(Parent.class,id);
        System.out.println("find: "+find);
    }
}
