package com.example.ch06jpastart14.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableGenerator(
        name ="product_table_generator",
        table = "product_sequences",
        pkColumnValue = "product_seq",
        allocationSize = 1
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_table_generator")
    @Column(name ="product_id")
    private Long id;

    @Setter
    private String name;

    @Setter
    private int price;

    //product가 member에서 무엇이라 불리는지 매칭
    @ManyToMany(mappedBy = "products")
    @ToString.Exclude
    @Setter
    private List<Member> members = new ArrayList<>();
}
