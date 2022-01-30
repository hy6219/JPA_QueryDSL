package com.example.ch06jpastart7.test;

import com.example.ch06jpastart7.domain.entity.Member;
import com.example.ch06jpastart7.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ManyToOneBiDirTest {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory=
                Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager=
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

        Member member1 = new Member();
        member1.setUsername("mem1");
        member1.setTeam(team);
        entityManager.persist(member1);

        Member member2 = new Member();
        member2.setUsername("mem2");
        member2.setTeam(team);
        entityManager.persist(member2);

        Team findTeam = entityManager.find(Team.class,1L);

        System.out.println("find members by team: "+findTeam.getMembers());
        //member에서 team을 다시 확인해보기
        findTeam.getMembers().forEach(member -> {
            System.out.println("team->members->team: "+member.getTeam());
        });
    }
}
