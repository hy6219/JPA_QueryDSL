package com.example.ch07jpastart14.domain.entity;

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

    @ManyToMany
    @JoinTable(
            name = "parent_child",
            joinColumns = {
                    //조인테이블에서 불릴 이름 name, 현재 엔티티에서 불리는 이름 referencedColumnName
                    @JoinColumn(name = "PARENT_ID",referencedColumnName = "PARENT_ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "CHILD_ID",referencedColumnName = "CHILD_ID")
            }
    )
    private List<Child> child = new ArrayList<>();
}
