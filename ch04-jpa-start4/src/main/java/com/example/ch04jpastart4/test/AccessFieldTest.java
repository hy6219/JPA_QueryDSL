package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.MemberAccessField;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class AccessFieldTest {
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        //3.EntityTransaction
        EntityTransaction tx=
                entityManager.getTransaction();

        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.로직시작
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
    static void logic(EntityManager entityManager){
        MemberAccessField member = new MemberAccessField();

        member.setData1("data1");
        member.setData2("data2");

        entityManager.persist(member);

        List<MemberAccessField> list=
                entityManager.createQuery("select m from MemberAccessField  m",MemberAccessField.class)
                        .getResultList();

        list.forEach(i->{
            System.out.println("#: "+i.getData1()+", "+i.getData2());
        });
    }
}
