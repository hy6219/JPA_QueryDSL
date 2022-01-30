package com.example.ch06jpastart9.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@TableGenerator(
        name = "team_table_generator",
        table ="team_sequences",
        pkColumnValue = "team_seq",
        allocationSize = 1
)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "team_table_generator")
    @Column(name ="team_id")
    private Long id;

    @Setter
    private String name;


    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();


    public void addMember(Member member){
        this.members.add(member);

        //무한루프에 빠지지 않도록 체크
        if(member.getTeam()!=this){
            member.setTeam(this);
        }
    }
}
