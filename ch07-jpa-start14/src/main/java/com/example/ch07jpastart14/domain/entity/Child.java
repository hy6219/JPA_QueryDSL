package com.example.ch07jpastart14.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@TableGenerator(
        name = "child_table_generator",
        pkColumnValue = "parent_seq",
        allocationSize = 1
)
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "child_table_generator")
    @Column(name = "CHILD_ID")
    private Long id;
    private String name;
}
