package com.example.ch08jpastart1.test;

import com.example.ch08jpastart1.domain.entity.Member;
import com.example.ch08jpastart1.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class GraphProxyReqTest {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

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
        List<String> userNames = new ArrayList<>(List.of("김치찌개","된장찌개","청국장"));//추후 제거 작업을 용이하기 지원해주기 위해서 arraylist로 감싸주기
        addMemberAndTeam(entityManager,userNames,"음식");
        printUserAndTeam(entityManager,1L);
        printUser(entityManager,1L);
    }

    /**
     * 회원, 팀 데이터 추가
     * @param entityManager
     */
    static void addMemberAndTeam(EntityManager entityManager, List<String> userNames, String teamName){

        Team team = new Team();
        team.setName(teamName);
        entityManager.persist(team);

        for (String s : userNames) {
            Member member = new Member();
            member.setUsername(s);
            member.setTeam(team);
            entityManager.persist(member);
        }
    }

    /**
     * 회원정보와 팀 정보를 조회
     * @param entityManager
     * @param memberId
     */
    static void printUserAndTeam(EntityManager entityManager, Long memberId){
        Member member = entityManager.find(Member.class,memberId);
        Team team = member.getTeam();
        StringBuilder sb = new StringBuilder();
        sb.append("팀 이름: ")
                .append(team.getName())
                .append(", 회원 이름: ")
                .append(member.getUsername());
        System.out.println(sb);
    }

    /**
     * 회원정보만 조회
     * @param entityManager
     * @param memberId
     */
    static void printUser(EntityManager entityManager, Long memberId){
        Member member = entityManager.find(Member.class,memberId);
        StringBuilder sb = new StringBuilder();
        sb.append("회원 이름: ")
                .append(member.getUsername());
        System.out.println(sb);
    }
}
