package com.example.ch04jpastart2.jpabook.start;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "article")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableGenerator(
        name = "ARTICLE_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "ARTICLE_SEQ",
        allocationSize = 1
)
public class Article {
    /**
     * TABLE 생성전략 연습용 Article 엔티티
     *
     * @author gs813
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "ARTICLE_SEQ_GENERATOR")
    private Long id;

    @Column(name = "writer")
    private String writer;

    @Column(name = "content")
    private String content;
}
