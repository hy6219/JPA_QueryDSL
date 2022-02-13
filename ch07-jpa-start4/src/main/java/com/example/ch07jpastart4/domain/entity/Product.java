package com.example.ch07jpastart4.domain.entity;

import com.example.ch07jpastart4.domain.constant.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@ToString
@TableGenerator(
        name ="product_table_generator",
        pkColumnValue = "product_sequences",
        allocationSize = 1
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_table_generator")
    @Column(name = "product_id")
    private Long id;

    @Setter
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Setter
    private int price;

    /*
    Many To One' attribute type should not be 'Mapped Superclass'
     */
}
