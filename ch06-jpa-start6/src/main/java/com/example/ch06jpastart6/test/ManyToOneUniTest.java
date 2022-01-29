package com.example.ch06jpastart6.test;

import com.example.ch06jpastart6.domain.entity.Member;
import com.example.ch06jpastart6.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ManyToOneUniTest {
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

    static void logic(EntityManager entityManager){
        Team team = new Team();
        team.setName("team1");
        entityManager.persist(team);

        Member member = new Member();
        member.setUsername("member1");
        member.setTeam(team);
        entityManager.persist(member);

        Member findMember =
                entityManager.find(Member.class,2L);

        Team findTeam = findMember.getTeam();

        System.out.println("find team: "+findTeam);
    }
}
