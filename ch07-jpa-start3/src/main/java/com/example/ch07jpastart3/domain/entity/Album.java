package com.example.ch07jpastart3.domain.entity;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Album extends Item{
    private String artist;
}
