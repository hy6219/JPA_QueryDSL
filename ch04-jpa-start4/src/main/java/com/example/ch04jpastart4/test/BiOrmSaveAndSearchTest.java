package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.BiMember;
import com.example.ch04jpastart4.domain.entity.BiTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * 다대일 양방향 연관관계 테스트
 */
public class BiOrmSaveAndSearchTest {
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
        //저장
        save(entityManager);
        //조회(객체 그래프 탐색)
        graphSearch(entityManager);
    }

    static void save(EntityManager entityManager){
        BiTeam team = new BiTeam();
        team.setName("bi-team1");
        entityManager.persist(team);

        BiMember member1 =new BiMember();
        member1.setUsername("member1");
        member1.setTeam(team);
        entityManager.persist(member1);

        BiMember member2 =new BiMember();
        member2.setUsername("member2");
        member2.setTeam(team);
        entityManager.persist(member2);


    }

    static void graphSearch(EntityManager entityManager){
        BiTeam team = entityManager.find(BiTeam.class,1L);
        List<BiMember> members = team.getMembers();//객체 그래프탐색(팀->회원)
        members.forEach(i->{
            System.out.println("# member name: "+i.getUsername());
        });
    }
}
