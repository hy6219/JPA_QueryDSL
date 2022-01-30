package com.example.ch06jpastart9.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableGenerator(
        name = "member_table_generator",
        table = "member_sequences",
        pkColumnValue = "member_seq",
        allocationSize = 1
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_table_generator")
    @Column(name = "member_id")
    private Long id;

    @Setter
    private String username;

    //읽기전용
    @ManyToOne
    @JoinColumn(name ="team_id",insertable = false,updatable = false)//fk키값
    @ToString.Exclude
    private Team team;

    public void setTeam(Team team){
        if(this.team != null){
            this.team.getMembers().remove(this);//지금 member와 연결 초기화
        }

        this.team = team;
        this.team.getMembers().add(this);
    }
}
