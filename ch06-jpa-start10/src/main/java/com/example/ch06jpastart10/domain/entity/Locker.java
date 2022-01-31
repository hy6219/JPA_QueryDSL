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
        name ="locker_table_generator",
        table = "locker_sequences",
        pkColumnValue = "locker_seq",
        allocationSize = 1
)
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "locker_table_generator")
    @Column(name ="locker_id")
    private Long id;

    @Setter
    private String name;
}
