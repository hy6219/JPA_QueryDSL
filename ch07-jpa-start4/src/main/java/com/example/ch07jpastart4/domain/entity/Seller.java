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
        @AttributeOverride(name ="id", column = @Column(name = "SELLER_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "SELLER_NAME"))
})
@AssociationOverrides({
        @AssociationOverride(name = "product",joinColumns = {@JoinColumn(name = "product_id")})
})
public class Seller extends BaseEntity{
    /*
    BaseEntity 상속 => id, name 상속
     */
    @Setter
    @Column(name = "shop_name")
    private String shopName;
}
