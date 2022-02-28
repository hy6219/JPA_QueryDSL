package com.example.ch07jpastart5.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(value = ParentComplexId.class)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Parent {
    /**
     * 복합키클래스에서의 필드명과 맞춰주어야 함
     */
    @Id
    @Column(name ="PARENT_ID1")
    private String id1;

    @Id
    @Column(name = "PARENT_ID2")
    private String id2;

    @Column(name = "NAME")
    private String name;
}
