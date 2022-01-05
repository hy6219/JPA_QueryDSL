package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class DetachTestByClose {
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체 생성
        EntityManagerFactory emf=
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체 생성
        EntityManager em=
                emf.createEntityManager();
        //3.EntityTransaction 객체 생성
        EntityTransaction tx=
                em.getTransaction();

        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.로직 테스트
            //////////////////////////////////////////////////////////////////////////////////////////////////
            Member find=em.find(Member.class,1);
            tx.commit();
            System.out.println("find: "+find);
            //////////////////////////////////////////////////////////////////////////////////////////////////
            //6.로직테스트-영속성 컨텍스트 종료
            em.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        emf.close();
    }


}
