package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.Board;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TemporalTest {
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
            //4.트랜잭션 시작
            tx.begin();
            //5.로직
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //7.entitymanager 종료
            entityManager.close();
        }
        //8.entitymanagerfactory 종료
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager){
        //1.비영속 상태의 Board 엔티티 3개 만들기
        Board board1 = new Board();
        Board board2 = new Board();
        Board board3 = new Board();

        Date today = new Date();
        Calendar current = Calendar.getInstance();

        board1.setTitle("title1");
        board1.setContent("content1");
        board1.setTemporalTypeDate(today);
        board1.setTemporalTypeTime(today);
        board1.setTemporalTypeTimeStamp(current);

        board2.setTitle("title2");
        board2.setContent("content2");
        board2.setTemporalTypeDate(today);
        board2.setTemporalTypeTime(today);
        board2.setTemporalTypeTimeStamp(current);

        board3.setTitle("title3");
        board3.setContent("content3");
        board3.setTemporalTypeDate(today);
        board3.setTemporalTypeTime(today);
        board3.setTemporalTypeTimeStamp(current);

        System.out.println("transient state of board1: "+board1);
        System.out.println("transient state of board2: "+board2);
        System.out.println("transient state of board3: "+board3);

        //2.make managed state
        entityManager.persist(board1);
        entityManager.persist(board2);
        entityManager.persist(board3);

        //3.영속성 컨텍스트에 저장된 모든 Board 엔티티를 확인하기
        List<Board> boards =
                entityManager.createQuery("select b from Board b",Board.class)
                        .getResultList();

        System.out.println("영속성 컨텍스트의 추적/관리를 받는 모든 Board 엔티티들 : "+boards);
    }
}
