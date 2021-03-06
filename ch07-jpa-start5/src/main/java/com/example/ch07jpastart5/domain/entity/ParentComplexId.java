package com.example.ch07jpastart5.domain.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ParentComplexId implements Serializable {
    private String id1;//자식클래스 필드명과 맞춰주어야 함
    private String id2;
}
