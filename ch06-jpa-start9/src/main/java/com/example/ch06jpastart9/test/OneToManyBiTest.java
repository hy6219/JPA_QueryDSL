package com.example.ch06jpastart9.test;

import com.example.ch06jpastart9.domain.entity.Member;
import com.example.ch06jpastart9.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class OneToManyBiTest {
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

        Member member1 = new Member();
        member1.setUsername("member1");
        member1.setTeam(team);
        entityManager.persist(member1);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setTeam(team);
        entityManager.persist(member2);

        Team findTeam = entityManager.find(Team.class,1L);
        List<Member> findByTeam = findTeam.getMembers();
        System.out.println("team으로 접근된 members: "+findByTeam);

        Member findMember = entityManager.find(Member.class,1L);
        Team findByMember = findMember.getTeam();
        System.out.println("member로 접근된 team: "+findByMember);
    }
}
