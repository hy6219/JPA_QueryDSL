package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.OrmMember;
import com.example.ch04jpastart4.domain.entity.OrmTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class OrmSaveTest {
    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("jpabook");
        EntityManager em=
                emf.createEntityManager();

        EntityTransaction tx =
                em.getTransaction();

        try {
            tx.begin();
            save(em);
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }

    static void save(EntityManager entityManager){
        OrmTeam team = new OrmTeam();
        team.setName("team1");
        entityManager.persist(team);//팀저장

        //멤버들 저장
        OrmMember member1= new OrmMember();
        member1.setUsername("member1");
        member1.setTeam(team);

        OrmMember member2 = new OrmMember();
        member2.setUsername("member2");
        member2.setTeam(team);

        entityManager.persist(member1);
        entityManager.persist(member2);
    }
}
