package com.example.ch06jpastart16.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableGenerator(
        name = "order_table_generator",
        table = "order_sequences",
        pkColumnValue = "order_seq",
        allocationSize = 1
)
public class OrderTable {//연관관계의 주인
    //sql 키워드때문에 OrderTable로 명칭변경
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_table_generator")
    @Column(name ="order_id")
    private Long id;

    @Setter
    private int orderAmount;

    @Setter
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name ="product_id")
    @Setter
    private Product product;

    public void setMember(Member member){
        if(this.member != null){
            this.member.getOrders().remove(this);
        }
        this.member = member;
        this.member.getOrders().add(this);//역방향 적용
    }

    @PrePersist
    public void setDefaultValue(){
        if(this.orderDate == null){
            this.setOrderDate(LocalDateTime.now());
        }
    }
}
