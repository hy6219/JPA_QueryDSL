package com.example.ch06jpastart7.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="member_id")
    private Long id;

    @Setter
    private String username;

    @ManyToOne
    @JoinColumn(name ="team_id")//상대측 id
    private Team team;

    public void setTeam(Team team1){
        //변경되는 관계를 위한 초기화
        if(this.team !=null){
            this.team.getMembers().remove(this);
        }
        this.team = team1;
        team.getMembers().add(this);
    }
}
