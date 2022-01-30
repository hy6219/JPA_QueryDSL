package com.example.ch06jpastart7.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Setter
    private String name;

    @OneToMany(mappedBy = "team")//상대방인 Team이 나Member측에서 뭐라고 불리는지를 mappedBy에 맺어줌
    //StackOverflowError를 막기 위함
    @ToString.Exclude
    private List<Member> members = new ArrayList<>();
}
