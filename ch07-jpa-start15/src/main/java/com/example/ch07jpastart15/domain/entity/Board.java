package com.example.ch07jpastart15.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "BOARD")
@NoArgsConstructor
@Getter
@Setter
@TableGenerator(
        name = "board_table_generator",
        pkColumnValue = "board_seq",
        allocationSize = 1
)
//다른 테이블 연결
@SecondaryTables({
        @SecondaryTable(name = "BOARD_DETAIL")
})
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "board_table_generator")
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

    //content 필드에 연결
    @Column(table = "BOARD_DETAIL")
    private String content;
}
