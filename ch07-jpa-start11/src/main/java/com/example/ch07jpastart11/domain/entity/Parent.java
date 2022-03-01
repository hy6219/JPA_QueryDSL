package com.example.ch07jpastart11.domain.entity;

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
        name = "parent_table_generator",
        pkColumnValue = "parent_seq",
        allocationSize = 1
)
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "parent_table_generator")
    @Column(name ="PARENT_ID")
    private Long id;

    private String name;

    @OneToOne
    @JoinTable(
            //조인테이블 이름
            name = "parent_child",
            //현재 엔티티를 참조하는 외래키
            joinColumns = {
                    @JoinColumn(name = "PARENT_ID",referencedColumnName = "PARENT_ID")
            },
            //반대방향 엔티티를 참조하는 외래키
            inverseJoinColumns = {
                    @JoinColumn(name = "CHILD_ID")
            }
    )
    private Child child;
}
