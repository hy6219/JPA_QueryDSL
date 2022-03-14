package com.example.ch08jpastart1.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@TableGenerator(
        name = "team_table_generator",
        pkColumnValue = "team_seq",
        allocationSize = 1
)
@Getter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "team_table_generator")
    private Long id;

    @Setter
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
}
