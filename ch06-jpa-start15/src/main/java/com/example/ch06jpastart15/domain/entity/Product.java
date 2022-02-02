package com.example.ch06jpastart15.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableGenerator(
        name = "product_table_generator",
        table = "product_sequences",
        pkColumnValue = "product_seq",
        allocationSize = 1
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "product_table_generator")
    @Column(name = "product_id")
    private Long id;

    @Setter
    private String name;
}
