package domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    private String username;

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team;

    public void setTeam(Team team){
        //변경되는 관계를 위해 초기화
        if(this.team !=null){
            this.team.getMembers().remove(this);
        }
        this.team = team;
        team.getMembers().add(this);
    }
}
