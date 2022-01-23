package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.OrmMember;
import com.example.ch04jpastart4.domain.entity.OrmTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class OrmUpdateTest {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager) {
        save(entityManager);
        update(entityManager);
        checkAfterUpdate(entityManager);
    }

    static void save(EntityManager entityManager) {
        OrmTeam team = new OrmTeam();
        team.setName("team1");
        entityManager.persist(team);//팀저장

        //멤버들 저장
        OrmMember member1 = new OrmMember();
        member1.setUsername("member1");
        member1.setTeam(team);

        OrmMember member2 = new OrmMember();
        member2.setUsername("member2");
        member2.setTeam(team);

        entityManager.persist(member1);
        entityManager.persist(member2);
    }

    static void update(EntityManager entityManager) {
        OrmMember member =
                entityManager.find(OrmMember.class, 2L);

        //새로운 팀
        OrmTeam team = new OrmTeam();
        team.setName("team2");
        entityManager.persist(team);

        //팀 변경
        member.setTeam(team);//변경추적됨
    }

    static void checkAfterUpdate(EntityManager entityManager){
        String jpql = "select m from OrmMember m join m.team t where "+
                "t.name=:teamName";
        List<OrmMember> result =
                entityManager.createQuery(jpql,OrmMember.class)
                        .setParameter("teamName","team2")
                        .getResultList();
        System.out.println("team2 members: "+result);
    }
}
