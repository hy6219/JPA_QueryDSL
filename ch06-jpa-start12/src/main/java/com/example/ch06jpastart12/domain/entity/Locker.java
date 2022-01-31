package com.example.ch06jpastart12.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableGenerator(
        name = "locker_table_generator",
        table = "locker_sequences",
        pkColumnValue = "locker_seq",
        allocationSize = 1
)
public class Locker {//대상테이블->연관관계 주인으로 만들 것
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "locker_table_generator")
    @Column(name ="locker_id")
    private Long id;

    @Setter
    private String name;

    @OneToOne
    @JoinColumn(name = "member_id")
    @Setter
    @ToString.Exclude
    private Member member;
}

