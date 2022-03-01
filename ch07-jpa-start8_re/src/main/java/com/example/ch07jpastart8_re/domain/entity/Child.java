package com.example.ch07jpastart8_re.domain.entity;

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
public class Child {
    @EmbeddedId
    private ChildId id;

    @ManyToOne
    @MapsId("parentId")//ChildId.parentId와 연결
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;

    @Column(name = "name")
    private String name;
}
