package com.example.ch06jpastart11.domain.entity;

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
public class Locker {//대상테이블
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "locker_table_generator")
    @Column(name ="locker_id")
    private Long id;

    @Setter
    private String name;

    @OneToOne(mappedBy = "locker")//member.class에서 locker가 어떻게 되어있는지 확인
    @Setter
    @ToString.Exclude
    private Member member;
}
