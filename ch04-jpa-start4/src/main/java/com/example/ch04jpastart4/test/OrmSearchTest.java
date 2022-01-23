package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.OrmMember;
import com.example.ch04jpastart4.domain.entity.OrmTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class OrmSearchTest {
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

        /**
         * SELECT M.*
         * FROM MEMBER M
         * INNER JOIN ORMTEAM ON ORMMEMBER.TEAM_ID=TEAM_ID
         * WHERE ORMTEAM.NAME=(파라미터값)
         */
        String jpql ="select m from OrmMember m join m.team t where "+
                "t.name=:teamName";
        List<OrmMember> resultList =
                entityManager.createQuery(jpql,OrmMember.class)
                        .setParameter("teamName","team1")
                        .getResultList();
        System.out.println("jpql 결과==");
        for(OrmMember memberJ:resultList){
            System.out.println("# "+memberJ);
        }

        OrmMember memberG = entityManager.find(OrmMember.class,2L);
        OrmTeam ormTeam = memberG.getTeam();
        System.out.println("그래프 탐색으로 조회한 Member["+memberG+"]의 팀: "+ormTeam);

    }
}
