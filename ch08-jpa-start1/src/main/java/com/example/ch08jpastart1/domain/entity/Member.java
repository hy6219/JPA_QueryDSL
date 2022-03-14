package com.example.ch08jpastart1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@TableGenerator(
        name = "member_table_generator",
        pkColumnValue = "member_seq",
        allocationSize = 1
)
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_table_generator")
    private Long id;

    @Setter
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public void setTeam(Team team){
        //삭제된 관계영향 줄이기 위해서 기존 팀과의 관계를 제거
        if(this.team != null){
            this.team.getMembers().remove(this);
        }
        this.team = team;
        team.getMembers().add(this);
    }
}
