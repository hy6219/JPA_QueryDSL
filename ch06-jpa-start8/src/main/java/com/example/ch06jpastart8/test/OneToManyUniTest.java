package com.example.ch06jpastart8.test;

import com.example.ch06jpastart8.domain.entity.Member;
import com.example.ch06jpastart8.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class OneToManyUniTest {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();
        EntityTransaction tx =
                entityManager.getTransaction();

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

    public static void logic(EntityManager entityManager){
        Member member1= new Member();
        member1.setUsername("member1");
        entityManager.persist(member1);

        Member member2=new Member();
        member2.setUsername("member2");
        entityManager.persist(member2);

        Team team = new Team();
        team.setName("team1");
        team.setMembers(List.of(member1,member2));
        entityManager.persist(team);

        
    }
}
