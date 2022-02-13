package com.example.ch07jpastart2.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name ="DTYPE")
@TableGenerator(
        name = "item_table_generator",
        pkColumnValue = "item_sequences",
        allocationSize = 1
)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "item_table_generator")
    private Long id;

    private String name;
    private int price;
    private String artist;
    private String director;
    private String actor;
    private String author;
    private String isbn;
}
