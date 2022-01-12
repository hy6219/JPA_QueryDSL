package com.example.ch04jpastart4.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Table(name = "board")
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name ="BOARD_SEQ_GENERATOR", allocationSize = 1)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "BOARD_SEQ_GENERATOR")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    /*
    @Temporal 실습
    * */
    @Column(name = "temporal_type_date")
    @Temporal(TemporalType.DATE)
    private Date temporalTypeDate;

    @Column(name = "temporal_type_time")
    @Temporal(TemporalType.TIME)
    private Date temporalTypeTime;

    @Column(name = "temporal_type_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar temporalTypeTimeStamp;

    /**
     * @Lob 실습
     * */
    @Column(name = "clob_str")
    @Lob
    private String clobStr;

    @Column(name = "blob_bytes")
    @Lob
    private byte[] blobBytes;
}
