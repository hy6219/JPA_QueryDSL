package com.example.ch07jpastart8_re.domain.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Embeddable
public class GrandChildId implements Serializable {
    //GrandChild.child에서 `@MapsId(childId)`와 매핑
    @Embedded
    private ChildId childId;

    @Column(name = "GRANDCHILD_ID")
    private String id;
}
