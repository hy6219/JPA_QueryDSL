package com.example.ch06jpastart6.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="team_id")//이부분 미지정시 Team.id로 인식!!
    private Long id;

    private String name;
}
