package com.example.ch07jpastart3.test;

import com.example.ch07jpastart3.domain.entity.Album;
import com.example.ch07jpastart3.domain.entity.Book;
import com.example.ch07jpastart3.domain.entity.Item;
import com.example.ch07jpastart3.domain.entity.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class TablePerConcreteClassStrategyTest {
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

        Album album = new Album();
        album.setName("album0");
        album.setPrice(30000);
        album.setArtist("album");
        entityManager.persist(album);

        Book book = new Book();
        book.setName("book0");
        book.setPrice(20000);
        book.setAuthor("author0");
        book.setIsbn("1234567");
        entityManager.persist(book);

        Movie movie = new Movie();
        movie.setName("movie0");
        movie.setPrice(20000);
        movie.setActor("actor0");
        movie.setDirector("director0");
        entityManager.persist(movie);

        List<Item> items = entityManager.createQuery("select item from Item item",Item.class)
                .getResultList();
        System.out.println("find all items: "+items);

        Album findAlbum = entityManager.find(Album.class,1L);
        Book findBook = entityManager.find(Book.class,2L);
        Movie findMovie = entityManager.find(Movie.class,3L);

        System.out.println("find album: "+findAlbum);
        System.out.println("find book: "+findBook);
        System.out.println("find movie: "+findMovie);
    }
}
