package com.example.ch07jpastart1.test;

import com.example.ch07jpastart1.domain.entity.Album;
import com.example.ch07jpastart1.domain.entity.Book;
import com.example.ch07jpastart1.domain.entity.Item;
import com.example.ch07jpastart1.domain.entity.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JoinedStrategyTest {
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    static void logic(EntityManager entityManager){
        Item item = new Item();
        item.setName("item1");
        item.setPrice(20000);
        entityManager.persist(item);

        Album album = new Album();
        album.setArtist("artist");
        album.setName("abc");
        album.setPrice(30000);
        entityManager.persist(album);

        Item findItem = entityManager.find(Item.class,1L);
        Album findAlbum = entityManager.find(Album.class,2L);

        System.out.println("findItem: "+findItem);
        System.out.println("findAlbum: "+findAlbum);
    }
}
