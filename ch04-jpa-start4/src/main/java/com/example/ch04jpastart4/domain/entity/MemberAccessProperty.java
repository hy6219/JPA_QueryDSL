package com.example.ch04jpastart4.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Access(AccessType.PROPERTY)
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "member_property_generator",
        allocationSize = 1)
public class MemberAccessProperty {

    private Long id;
    @Getter
    private String data1;
    @Getter
    private String data2;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "member_property_generator")
    public Long getId() {
        return this.id;
    }
}
