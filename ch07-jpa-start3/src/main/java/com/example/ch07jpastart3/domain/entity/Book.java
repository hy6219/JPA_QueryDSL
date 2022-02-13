package com.example.ch07jpastart3.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "book_id")
public class Book extends Item{
    private String author;
    private String isbn;
}
