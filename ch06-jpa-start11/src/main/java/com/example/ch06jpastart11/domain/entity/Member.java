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
    
    @OneToOne
    @JoinColumn(name ="locker_id")//연관관계 주인측(fk이름)
    @Setter
    @ToString.Exclude
    private Locker locker;
}
