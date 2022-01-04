package com.example.ch02jpastart1.jpabook.start;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name="member")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="AGE")
    private Integer age;
}
