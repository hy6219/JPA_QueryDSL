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
        name ="member_table_generator",
        table = "member_sequences",
        pkColumnValue = "member_seq",
        allocationSize = 1
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_table_generator")
    @Column(name ="member_id")
    private Long id;

    @Setter
    private String username;

    @ManyToMany
    @JoinTable(
            name ="member_product",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name ="product_id")
    )
    @ToString.Exclude
    @Setter
    private List<Product> products = new ArrayList<>();

}
