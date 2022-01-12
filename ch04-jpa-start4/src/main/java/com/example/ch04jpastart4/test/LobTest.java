package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.Board;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class LobTest {
    public static void main(String[] args) {
        //1.EntityManagerFactory
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        //3.EntityTransaction
        EntityTransaction tx =
                entityManager.getTransaction();
        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.로직
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7.엔티티 매니저 종료
            entityManager.close();
        }
        //8.엔티티 매니저 팩토리 종료
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager) {
        //1.transient state entities!
        Board board1 = new Board();
        Board board2 = new Board();
        byte[] bArr = {'a','b','c'};

        board1.setTitle("배고프다1");
        board1.setContent("배고픈 내용1");
        board1.setClobStr("CLOB 테스트1");
        board1.setBlobBytes(bArr);

        board2.setTitle("배고프다2");
        board2.setContent("배고픈 내용2");
        board2.setClobStr("CLOB 테스트2");
        board2.setBlobBytes(bArr);

        System.out.println("비영속 상태의 board1 : "+board1);
        System.out.println("비영속 상태의 board2 : "+board2);

        //2.영속성 컨텍스트의 관리를 받을 수 있도록 등록하기
        entityManager.persist(board1);
        entityManager.persist(board2);

        //3.영속성 컨텍스트에 등록한 board1, board2 확인하기
        Board find1 =
                entityManager.find(Board.class,1L);
        Board find2 =
                entityManager.find(Board.class,2L);

        System.out.println("find1 : "+find1);
        System.out.println("find2 : "+find2);
    }
}
