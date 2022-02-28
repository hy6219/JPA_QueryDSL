package com.example.ch07jpastart7.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@IdClass(value = ChildId.class)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Child {
    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;//ChildId.parent와 매핑

    @Id
    @Column(name = "CHILD_ID")
    private String childId;//ChildId.childId와 매핑

    @Column(name = "NAME")
    private String name;

}
