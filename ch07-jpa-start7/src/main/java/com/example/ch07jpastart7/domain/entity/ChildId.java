package com.example.ch07jpastart7.domain.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ChildId implements Serializable {
    //Child.parent 매핑
    private String parent;
    //Child.childId 매핑
    private String childId;
}
