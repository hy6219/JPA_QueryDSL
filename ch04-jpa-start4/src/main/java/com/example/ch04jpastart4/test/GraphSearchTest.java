package com.example.ch04jpastart4.test;

import com.example.ch04jpastart4.domain.entity.GraphSearchMember;
import com.example.ch04jpastart4.domain.entity.GraphSearchTeam;

public class GraphSearchTest {
    public static void main(String[] args) {
        GraphSearchTeam team = new GraphSearchTeam(1L,"team1");
        GraphSearchMember member = new GraphSearchMember();
        member.setId(1L);
        member.setUsername("member1");
        member.setTeam(team);

        GraphSearchTeam searchTeam =
                member.getTeam();
        System.out.println("단방향: "+searchTeam);
    }
}
