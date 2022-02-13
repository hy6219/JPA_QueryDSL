package com.example.ch07jpastart3.domain.entity;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Book extends Item{
    private String author;
    private String isbn;
}
