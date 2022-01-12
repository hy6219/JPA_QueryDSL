package com.example.ch04jpastart3.domain.entity;

import com.example.ch04jpastart3.common.RoleType;
import lombok.*;

import javax.persistence.*;

/**
 * @author gs813
 * (1) 회원 고유 식별자(PK)
 * (2) 회원 이름
 * (3) 회원 나이(null 값 허용)
 * (4) 회원 구분(RoleType)
 */
@Table(name = "member")
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "MEMBER_SEQ_GENERATOR",allocationSize = 1)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column
    private Integer age;

    @Column(name = "role_type")
    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;
}
