package com.example.ch06jpastart8.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@TableGenerator(
        name ="team_table_generator",
        table = "team_sequences",
        pkColumnValue = "team_seq",
        allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "team_table_generator")
    @Column(name ="team_id")
    private Long id;

    private String name;

    //일대다 단방향
    @OneToMany
    @JoinColumn(name = "team_id")//member 테이블의 fk
    private List<Member> members = new ArrayList<>();
}
