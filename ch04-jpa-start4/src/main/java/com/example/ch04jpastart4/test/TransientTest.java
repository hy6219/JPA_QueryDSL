package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.Board;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TransientTest {
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        //3.EntityTransaction 객체
        EntityTransaction tx =
                entityManager.getTransaction();

        try {
            //트랜잭션 시작
            tx.begin();
            //로직
            logic(entityManager);
            //트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager){
        Board board = new Board();

        board.setTitle("transient 테스트용 제목");
        board.setContent("transient 테스트용 내용");
        board.setTemp(1);

        //영속성 컨텍스트에 등록
        entityManager.persist(board);

        //영속성 컨텍스트에 등록된 Board 객체 조회하기
        Board find = entityManager.find(Board.class,1L);
        System.out.println("영속성 컨텍스트에 등록된 객체: "+find);
    }
}
