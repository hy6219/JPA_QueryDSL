package com.example.ch07jpastart4.test;

import com.example.ch07jpastart4.domain.constant.ProductType;
import com.example.ch07jpastart4.domain.entity.Member;
import com.example.ch07jpastart4.domain.entity.Product;
import com.example.ch07jpastart4.domain.entity.Seller;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class MappedSuperclassTest {
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

    static void logic(EntityManager entityManager) {

        Product product = new Product();
        product.setType(ProductType.COMPUTER);
        product.setName("samsung notebook");
        product.setPrice(1000000);
        entityManager.persist(product);

        Member member = new Member();
        member.setName("member1");
        member.setEmail("member1@mem.com");
        member.setProduct(product);
        entityManager.persist(member);


        Seller seller = new Seller();
        seller.setName("seller1");
        seller.setShopName("shop1");
        seller.setProduct(product);
        entityManager.persist(seller);

        List<Product> products = entityManager.createQuery("select p from Product p", Product.class)
                .getResultList();
        List<Member> members = entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();
        List<Seller> sellers = entityManager.createQuery("select s from Seller s", Seller.class)
                .getResultList();

        System.out.println("products: " + products);
        System.out.println("members: " + members);
        System.out.println("sellers: " + sellers);

        StringBuilder sb = new StringBuilder();
        sb.append("members-base entity info: ").append('\n');

        members.forEach(ele1 -> {
            sb.append("id: ")
                    .append(ele1.getId())
                    .append(", ")
                    .append(ele1.getName())
                    .append(", ")
                    .append(ele1.getProduct())
                    .append('\n');
        });

        sb.append("sellers-base entity info: ").append('\n');

        sellers.forEach(ele1 -> {
            sb.append("id: ")
                    .append(ele1.getId())
                    .append(", ")
                    .append(ele1.getName())
                    .append(", ")
                    .append(ele1.getProduct())
                    .append('\n');
        });

        System.out.print(sb);
    }
}
