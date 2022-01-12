package com.example.ch04jpastart3.test;

import com.example.ch04jpastart3.domain.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;

import static com.example.ch04jpastart3.common.RoleType.ADMIN;
import static com.example.ch04jpastart3.common.RoleType.USER;

public class EnumeratedTest {
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("jpabook");

        //2.EntityManager 객체
        EntityManager entityManager =
                entityManagerFactory.createEntityManager();

        //3.EntityTransaction 객체
        EntityTransaction tx =
                entityManager.getTransaction();


        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.로직
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //7.entitymanager 종료
            entityManager.close();
        }
        //8.entitymanagerfactory 종료
        entityManagerFactory.close();
    }

    public static void logic(EntityManager entityManager){
        //1.회원 엔티티 생성(비영속상태)
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();

        //member1은 age를 null로 넣어보자
        member1.setName("김길동");
        member1.setRoleType(ADMIN);

        member2.setName("나길동");
        member2.setAge(27);
        member2.setRoleType(USER);

        member3.setName("다길동");
        member3.setAge(28);
        member3.setRoleType(USER);


        //2.영속성 컨텍스트 관리 시작
        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);

        //3.영속성 컨텍스트가 관리하는 모든 member 엔티티 조회
        List<Member> members =
                entityManager.createQuery("select m from Member m",Member.class)
                        .getResultList();

        System.out.println("영속성 컨텍스트가 관리하는 모든 Member 엔티티들: "+members);
    }
}
