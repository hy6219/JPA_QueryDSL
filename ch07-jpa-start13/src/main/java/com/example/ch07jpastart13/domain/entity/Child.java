package com.example.ch07jpastart13.domain.entity;

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

    @ManyToOne(optional = false)//필수적 비식별 관계
    @JoinTable(
            name = "parent_child",
            joinColumns = {
                    //조인테이블에서 불릴 이름 name, 현재 엔티티에서 불리는 이름 referencedColumnName
                    @JoinColumn(name = "CHILD_ID",referencedColumnName = "CHILD_ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "PARENT_ID",referencedColumnName = "PARENT_ID")
            }
    )
    @ToString.Exclude
    private Parent parent;
}
