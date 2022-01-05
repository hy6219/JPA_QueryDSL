package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.*;
import java.util.List;

public class JPQLOverview {
    public static void main(String[] args) {
        /**
         * JPQL과 같은 객체 지향 쿼리를 활용했을 때 flush()가 자동 호출된다!
         * */
        //1.EntityManagerFactory 객체 만들기
        EntityManagerFactory emf=
                Persistence.createEntityManagerFactory("jpabook");
        //2.EntityManager 객체 만들기
        EntityManager em=
                emf.createEntityManager();
        //3.transaction
        EntityTransaction entityTransaction=
                em.getTransaction();

        try{
            entityTransaction.begin();

            //4. memberA,memberB, memberC 세개 객체를 만들어서 영속성 컨텍스트에 등록하기
            Member memberA=new Member();
            Member memberB=new Member();
            Member memberC=new Member();

            memberA.setId(2);
            memberA.setAge(20);
            memberA.setName("가길동");
            em.persist(memberA);

            memberB.setId(3);
            memberB.setAge(21);
            memberB.setName("나길동");
            em.persist(memberB);

            memberC.setId(4);
            memberC.setAge(22);
            memberC.setName("다길동");
            em.persist(memberC);


            System.out.println("삽입할 객체들: "+memberA);
            System.out.println("삽입할 객체들: "+memberB);
            System.out.println("삽입할 객체들: "+memberC);
            //5. JPQL 실행
            //중간이 쿼리 실행해서 자동으로 플러시 호출
            TypedQuery query=em.createQuery("select m from Member m",Member.class);
            List<Member> members=query.getResultList();

            members.forEach(i->{
                System.out.println("member: "+i);
            });


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }

    }
}
