package com.example.ch06jpastart8.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@TableGenerator(
        name = "member_seq_generator",
        table = "member_sequences",
        pkColumnValue = "member_seq",
        allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_seq_generator")
    @Column(name = "member_id")
    private Long id;

    private String username;
}
