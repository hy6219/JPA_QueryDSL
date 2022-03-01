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
        name = "board_detail_generator",
        pkColumnValue = "borad_detail_seq",
        allocationSize = 1
)
public class BoardDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "board_detail_generator")
    private Long id;

    private String content;

    @MapsId//Board엔티티 내부에서 BoardDetail.boardId 매핑->Board측과 연결될것
    @OneToOne
    @JoinColumn(name = "BOARD_ID")
    @ToString.Exclude
    private Board board;
}
