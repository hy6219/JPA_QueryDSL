package com.example.ch06jpastart10.domain.entity;

import lombok.*;

import javax.persistence.*;

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

    @OneToOne
    @JoinColumn(name ="locker_id")
    @Setter
    private Locker locker;//일대일 연관관계
}
