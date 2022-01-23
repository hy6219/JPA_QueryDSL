package com.example.ch04jpastart4.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GraphSearchMember {

    private Long id;

    private String username;
    private GraphSearchTeam team;
}
