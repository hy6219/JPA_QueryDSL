package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.OrmMember;
import com.example.ch04jpastart4.domain.entity.OrmTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * 엔티티 삭제
 */
public class OrmEntityRmTest {
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
        deleteEntity(entityManager);
        afterRmEntity(entityManager);
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

    static void deleteEntity(EntityManager entityManager){
        //연관관계먼저 끊어내기
        String jpql ="select m from OrmMember m join m.team t where "+
                "t.name=:teamName";

        OrmTeam team = entityManager.find(OrmTeam.class,1L);

        List<OrmMember> members =
                entityManager.createQuery(jpql,OrmMember.class)
                        .setParameter("teamName","team1")
                        .getResultList();

        members.forEach(i->{
            i.setTeam(null);
        });

        //연결된 팀 엔티티 제거
        entityManager.remove(team);
    }

    static void afterRmEntity(EntityManager entityManager){
        List<OrmMember> members =
                entityManager.createQuery("select m from OrmMember m",OrmMember.class)
                        .getResultList();
        System.out.println("## after delete: "+members);
    }
}
