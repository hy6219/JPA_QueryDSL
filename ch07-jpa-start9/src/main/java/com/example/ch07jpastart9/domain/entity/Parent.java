package com.example.ch07jpastart9.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Parent {
    @Id
    @Column(name = "PARENT_ID")
    private String id;

    @Column(name = "NAME")
    private String name;
}
