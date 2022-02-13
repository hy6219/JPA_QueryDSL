package com.example.ch07jpastart1.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("A")
@PrimaryKeyJoinColumn(name ="ALBUM_ID")
public class Album extends Item{
    private String artist;
}
