package com.example.ch07jpastart3.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TableGenerator(
        name = "item_table_generator",
        pkColumnValue = "item_sequences",
        allocationSize = 1
)
public abstract class Item {
    @Id
    @Column(name ="item_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "item_table_generator")
    private Long id;

    private String name;
    private int price;
}
