package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class DetachTestByClear {
    /**
     * @author JISOOJEONG
     * entityManager.clear를 활용해서 managed state를 clear state로 변경하기
     * */
    public static void main(String[] args) {
        //1.EntityManagerFactory 객체
        EntityManagerFactory emf=
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체
        EntityManager em=
                emf.createEntityManager();
        //3.EntityTransaction 객체
        EntityTransaction tx=
                em.getTransaction();

        try{
            //4.트랜잭션 시작
            tx.begin();

            //5.find 로직 시작
            logic(em);
            //6.트랜잭션 커밋
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }

    public static void logic(EntityManager entityManager){
        //1.영속성 컨텍스트로 등록
        Member member=new Member();
        member.setId(2);
        member.setAge(26);
        member.setName("가나다");

        entityManager.persist(member);

        //2.find로 등록된 객체 조회하기
        Member findMember=entityManager.find(Member.class,2);
        System.out.println("찾은 객체: "+findMember);

        //3.영속성 컨텍스트 초기화
        entityManager.clear();
        //4.모든 객체를 확인해보자
        List<Member> members=
                entityManager.createQuery("select m from Member m",Member.class)
                        .getResultList();

        System.out.println("지금 저장된 모든 데이터를 조회="+members);
    }
}
