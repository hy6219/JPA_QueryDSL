package com.example.ch04jpastart2.jpabook.start;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1, allocationSize = 1
)
@Table(name = "board")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            ,generator = "BOARD_SEQ_GENERATOR")
    private Long id;

    @Column(name = "writer")
    private String writer;

    @Column(name = "contents")
    private String contents;
}
