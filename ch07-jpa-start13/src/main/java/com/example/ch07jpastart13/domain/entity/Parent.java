package com.example.ch07jpastart13.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@TableGenerator(
        name = "parent_table_generator",
        pkColumnValue = "parent_seq",
        allocationSize = 1
)
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "parent_table_generator")
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Child> child = new ArrayList<>();
}
