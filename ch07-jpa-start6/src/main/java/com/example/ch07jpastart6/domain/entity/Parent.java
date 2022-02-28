package com.example.ch07jpastart6.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
public class Parent {
    @EmbeddedId
    private ParentId id;

    @Column(name = "name")
    private String name;
}
