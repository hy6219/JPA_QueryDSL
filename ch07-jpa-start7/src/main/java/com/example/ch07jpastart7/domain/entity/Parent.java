package com.example.ch07jpastart7.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Setter
@Getter
@ToString
public class Parent {
    @Id
    @Column(name = "PARENT_ID")
    private String id;

    private String name;
}
