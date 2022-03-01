package com.example.ch07jpastart15.test;

import com.example.ch07jpastart15.domain.entity.Board;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 엔티티하나에여러테이블테스트 {
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
        board.setContent("content1");
        board.setTitle("title1");
        entityManager.persist(board);

        Board find = entityManager.find(Board.class,1L);
        System.out.println("board: "+find);
    }
}
