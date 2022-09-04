package com.example.jpql.domain;

import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Entity(name = "Member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Setter
    @Column(name = "name")
    private String username;
}
