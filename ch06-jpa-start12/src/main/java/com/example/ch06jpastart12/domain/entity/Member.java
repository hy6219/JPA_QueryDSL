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
        name = "member_table_generator",
        table = "member_sequences",
        pkColumnValue = "member_seq",
        allocationSize = 1
)
public class Member {//주테이블
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_table_generator")
    @Column(name = "member_id")
    private Long id;

    @Setter
    private String username;

    @OneToOne(mappedBy = "member")//Member가 Locker에 어떻게 되어 있는지 지정(연관관계 주인이 Locker이므로 Member는 mappedBy로 받기)
    @Setter
    @ToString.Exclude
    private Locker locker;
}