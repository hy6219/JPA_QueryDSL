package com.example.ch07jpastart10.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@TableGenerator(
        name = "board_table_generator",
        pkColumnValue = "borad_seq",
        allocationSize = 1
)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "board_table_generator")
    private Long id;

    private String title;

    @OneToOne(mappedBy = "board")
    @ToString.Exclude
    private BoardDetail boardDetail;
}
