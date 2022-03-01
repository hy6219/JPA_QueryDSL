package com.example.ch07jpastart8_re.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GrandChild {
    @EmbeddedId
    private GrandChildId id;

    @MapsId("childId")//GrandChildId.childId와 연결
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;

    @Column(name = "name")
    private String name;
}
