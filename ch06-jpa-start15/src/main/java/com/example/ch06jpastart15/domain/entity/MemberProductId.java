package com.example.ch06jpastart15.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 복합키 클래스
 * - serializable,기본 생성자, equals,hashCode 가져야!
 * - 식별자 클래스는 public!!
 */
@NoArgsConstructor
@Setter
@EqualsAndHashCode
public class MemberProductId implements Serializable {

    private Long member;//MemberProduct.member와 연결
    private Long product;//MemberProduct.product와 연결
}
