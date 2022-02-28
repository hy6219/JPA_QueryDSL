package com.example.ch07jpastart7.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GrandChildId implements Serializable {
    //GrandChild.child와 매핑
    private ChildId child;
    //GrandChild.grandChildId와 매핑
    private String grandChildId;
}
