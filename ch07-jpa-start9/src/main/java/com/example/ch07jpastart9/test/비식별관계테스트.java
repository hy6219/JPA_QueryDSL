package com.example.ch07jpastart9.test;

import com.example.ch07jpastart9.domain.entity.Child;
import com.example.ch07jpastart9.domain.entity.GrandChild;
import com.example.ch07jpastart9.domain.entity.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 비식별관계테스트 {
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
        parent.setId("pp");
        parent.setName("ppppppppp");
        entityManager.persist(parent);

        Child child = new Child();
        child.setId("cc");
        child.setParent(parent);
        child.setName("ccccccc");
        entityManager.persist(child);

        GrandChild grandChild = new GrandChild();
        grandChild.setId("gg");
        grandChild.setChild(child);
        grandChild.setName("gg");
        entityManager.persist(grandChild);
        //==> 이전단계에서의 PK를 FK로써만 참고할 뿐~~

        GrandChild findGrand = entityManager.find(GrandChild.class,"gg");
        System.out.println("findGrand: "+findGrand);
        //그래프 탐색
        System.out.println("find child by grand : "+findGrand.getChild());
        System.out.println("find parent by grand: "+findGrand.getChild().getParent());
    }
}
