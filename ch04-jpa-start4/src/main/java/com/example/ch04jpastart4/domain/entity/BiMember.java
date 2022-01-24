package com.example.ch04jpastart4.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    //연관관계
    @ManyToOne
    @JoinColumn(name ="biteam_id")
    private BiTeam team;
}
