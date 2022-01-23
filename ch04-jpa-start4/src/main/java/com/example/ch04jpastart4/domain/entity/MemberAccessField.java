package com.example.ch04jpastart4.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "member_field_generator",
        allocationSize = 1)
public class MemberAccessField {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "member_field_generator")
    private Long id;

    @Access(AccessType.FIELD)
    private String data1;
    @Access(AccessType.FIELD)
    private String data2;
}
