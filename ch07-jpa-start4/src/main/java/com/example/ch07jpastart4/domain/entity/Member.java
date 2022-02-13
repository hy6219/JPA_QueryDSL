package com.example.ch07jpastart4.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@ToString
@AttributeOverrides({
        @AttributeOverride(name ="id", column = @Column(name = "MEMBER_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME"))
})
@AssociationOverrides({
        @AssociationOverride(name = "product",joinColumns = {@JoinColumn(name = "product_id")})
})
public class Member extends BaseEntity{
    /*
    BaseEntity 상속 => id, name 상속
     */
    @Setter
    private String email;
}
