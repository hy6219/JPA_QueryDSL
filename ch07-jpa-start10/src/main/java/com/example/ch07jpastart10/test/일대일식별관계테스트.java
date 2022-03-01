package com.example.ch07jpastart10.test;

import com.example.ch07jpastart10.domain.entity.Board;
import com.example.ch07jpastart10.domain.entity.BoardDetail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 일대일식별관계테스트 {
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
        Board board = new Board();
        board.setTitle("board1");
        entityManager.persist(board);

        BoardDetail detail = new BoardDetail();
        detail.setBoard(board);
        detail.setContent("detail");
        board.setBoardDetail(detail);
        entityManager.persist(detail);

        BoardDetail find = entityManager.find(BoardDetail.class,1L);
        System.out.println("board detail: "+find);
        System.out.println("board by board detail : "+find.getBoard());
    }
}
