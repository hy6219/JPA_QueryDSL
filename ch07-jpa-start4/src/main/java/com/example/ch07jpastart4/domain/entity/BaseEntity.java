package com.example.ch07jpastart4.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@MappedSuperclass
@NoArgsConstructor
@Getter
@ToString
@TableGenerator(
        name = "base_entity_generator",
        pkColumnValue = "base_entity_sequences",
        allocationSize = 1
)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "base_entity_generator")
    private Long id;

    @Setter
    private String name;

    @ManyToOne
    @JoinColumn(name = "product")
    @Setter
    private Product product;
}
