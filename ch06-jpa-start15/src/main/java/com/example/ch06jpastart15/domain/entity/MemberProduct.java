package com.example.ch06jpastart15.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
//복합키 이용
@IdClass(MemberProductId.class)
public class MemberProduct {
    @Id
    @ManyToOne
    @JoinColumn(name ="member_id")
    @Setter
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    @Setter
    private Product product;

    @Setter
    private int orderAmount;
}
