package com.example.ch04jpastart2.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class IdentityStrategyTest {
    //1.EntityManagerFactory 객체
    static EntityManagerFactory emf=
            Persistence.createEntityManagerFactory("jpabook");
    //2,EntityManager 객체
    static EntityManager em=
            emf.createEntityManager();
    //3.EntityTransaction 객체
    static EntityTransaction tx=
            em.getTransaction();

    public static void main(String[] args) {
        try{
            //4.트랜잭션 시작
            tx.begin();
            //5.로직테스트
            Member member=new Member();
            member.setAge(20);
            member.setName("가나다");
            //영속성 컨텍스트로 등록
            em.persist(member);
            //모든 데이터 확인
            List<Member> members=
                    em.createQuery("select m from Member m",Member.class)
                            .getResultList();
            System.out.println("identity 자동 생성 전략 적용 후 확인: "+members);
            //6.커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }
}
