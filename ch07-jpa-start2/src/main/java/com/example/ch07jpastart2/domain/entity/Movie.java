package com.example.ch07jpastart2.domain.entity;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@NoArgsConstructor
@DiscriminatorValue("movie")
@PrimaryKeyJoinColumn(name ="MOVIE_ID")
public class Movie extends Item{
}
