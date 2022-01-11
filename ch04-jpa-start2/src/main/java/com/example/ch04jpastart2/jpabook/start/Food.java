package com.example.ch04jpastart2.jpabook.start;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "food")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
}
