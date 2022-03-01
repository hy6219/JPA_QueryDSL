package com.example.ch07jpastart8_re.domain.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Embeddable
public class ChildId implements Serializable {
    //Child.parent에서 `@MapsId(parentId)`와 매핑
    private String parentId;

    @Column(name = "CHILD_ID")
    private String id;
}
