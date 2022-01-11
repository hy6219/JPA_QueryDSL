package com.example.ch04jpastart2.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class SequenceStrategyTest {

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
            //5.테스트
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager) {
        //Board 객체 하나를 영속성 컨텍스트에 등록하기
        Board board = new Board();
        board.setWriter("홍길동1");
        board.setContents("홍길동1의 영속성 컨텍스트 등록되기");

        entityManager.persist(board);

        //등록된 모든 board 찾기
        List<Board> boards = entityManager.createQuery("select b from Board b")
                .getResultList();

        System.out.println("등록된 모든 boards: " + boards);
    }
}
