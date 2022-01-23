package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.OrmMember;
import com.example.ch04jpastart4.domain.entity.OrmTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * 연관관계 제거
 */
public class OrmRelationDelTest {
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

    static void logic(EntityManager entityManager){
        save(entityManager);
        removeRel(entityManager);
        afterRemove(entityManager);
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

    static void removeRel(EntityManager entityManager){
        OrmMember member = entityManager.find(OrmMember.class,2L);
        member.setTeam(null);//변경추적됨
    }

    static void afterRemove(EntityManager entityManager){
        List<OrmMember> list =
                entityManager.createQuery("select m from OrmMember m join m.team t",OrmMember.class)
                        .getResultList();
        System.out.println("after remove rel: "+list);
    }
}
