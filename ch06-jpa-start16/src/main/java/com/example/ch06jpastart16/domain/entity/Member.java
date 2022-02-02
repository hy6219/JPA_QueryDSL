package com.example.ch06jpastart16.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private List<OrderTable> orders = new ArrayList<>();
}
