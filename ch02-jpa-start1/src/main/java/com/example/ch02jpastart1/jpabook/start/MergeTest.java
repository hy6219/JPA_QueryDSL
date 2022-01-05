package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class MergeTest {
    //1.EntityManagerFactory 객체
    static EntityManagerFactory emf=
            Persistence.createEntityManagerFactory("jpabook");
    //2.EntityManager 객체
    static EntityManager em=
            emf.createEntityManager();
    //3.EntityTransaction 객체
    static EntityTransaction tx=
            em.getTransaction();

    public static void main(String[] args) {
        try{
            //4.트랜잭션 시작
            tx.begin();
            //5.영속성 컨텍스트에 등록
            Member member=new Member();
            member.setId(4);
            member.setName("가길동");
            member.setAge(30);

            em.persist(member);
            //6.저장된 모든 데이터 확인
            List<Member> members=
                    em.createQuery("select m from Member m",Member.class)
                            .getResultList();
            System.out.println("영속성 컨텍스트 등록: "+members);
            //7.영속성 컨텍스트 초기화 -> 준영속상태로 만들기
            em.clear();
            //8.준영속상태에서 다시 영속상태로 만들기
            //일부 값 변경
            member.setName("수정된 가길동");
            //merge
            em.merge(member);
            //저장된 모든 데이터 다시 확인
            members=
                    em.createQuery("select m from Member m",Member.class)
                            .getResultList();
            System.out.println("준영속 상태에서 다시 영속 상태로 만들기: "+members);
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }
}
