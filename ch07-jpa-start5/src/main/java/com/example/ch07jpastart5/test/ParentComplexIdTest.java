package com.example.ch07jpastart5.test;

import com.example.ch07jpastart5.domain.entity.Parent;
import com.example.ch07jpastart5.domain.entity.ParentComplexId;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ParentComplexIdTest {
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
        parent.setId1("parent_id1");
        parent.setId2("parent_id2");
        parent.setName("name1");
        entityManager.persist(parent);

        List<Parent> saved = entityManager.createQuery("select p from Parent p",Parent.class)
                .getResultList();

        System.out.println("저장되었던 모든 Parents: "+saved);

        //ParentComplexId로 조회
        ParentComplexId complexId = new ParentComplexId();
        complexId.setId1("parent_id1");
        complexId.setId2("parent_id2");
        Parent findByComplexId = entityManager.find(Parent.class,complexId);
        System.out.println("ParentComplexId로 조회: "+findByComplexId);
    }
}
