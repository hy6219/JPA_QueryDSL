package com.example.ch07jpastart1.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * warning: Generating equals/hashCode implementation but without a call to superclass,
 * even though this class does not extend java.lang.Object. If this is intentional,
 * add '@EqualsAndHashCode(callSuper=false)' to your type.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("M")
public class Movie extends Item {
    private String director;
    private String actor;
}
