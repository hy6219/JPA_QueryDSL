package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        //1.엔티티 매니저 팩토리 생성
        EntityManagerFactory entityManagerFactory=
                Persistence.createEntityManagerFactory("jpabook");

        //2.엔티티 매니저 생성
        EntityManager entityManager=
                entityManagerFactory.createEntityManager();

        //3.트랜잭션 획득
        EntityTransaction tx=
                entityManager.getTransaction();

        try {
            //4.트랜잭션 시작
            tx.begin();
            //5.비즈니스 로직 실행
            logic(entityManager);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //7.엔티티 매니저 종료
            entityManager.close();
        }

        //8.엔티티 매니저 팩토리 종료
        entityManagerFactory.close();
    }
    //비즈니스 로직
    private static void logic(EntityManager entityManager){
        Member member=new Member();
        member.setId(1);
        member.setAge(22);
        member.setName("홍길동");

        //등록
        entityManager.persist(member);
        //수정
        member.setAge(26);

        //한건 조회
        Member findMember=entityManager.find(Member.class,1);
        System.out.println("findMember="+findMember);

        //목록조회
        List<Member> members=entityManager.createQuery("select m from Member m",Member.class)
                .getResultList();
        System.out.println("members: "+members);

        //삭제
        entityManager.remove(member);
    }
}
