package com.example.ch02jpastart1.jpabook.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class TransientToManagedTest {
    /**
     * @author JISOOJEONG
     * merge(entity) 메서드를 활용해서 비영속 상태의 엔티티를
     * 영속상태로 만들기
     * */
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
            //5.비영속 엔티티를 영속 상태로 변경해주기
            Member member=new Member();
            member.setId(5);
            member.setAge(22);
            member.setName("def");
            //6.new/transient state to managed state
            em.merge(member);
            //7.저장된 모든 데이터 확인해보기
            List<Member> members=
                    em.createQuery("select m from Member m",Member.class)
                            .getResultList();
            System.out.println("비영속 상태에서 영속 상태로 변경하기: "+members);
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }
}
