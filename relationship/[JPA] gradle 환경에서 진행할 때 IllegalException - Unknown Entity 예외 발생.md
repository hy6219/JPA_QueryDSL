# [JPA] gradle 환경에서 진행할 때 IllegalException - Unknown Entity 예외 발생

gradle에서 엔티티 어노테이션을 붙여 테스트해보던 중 "IllegalException - Unknown Entity" 예외가 발생해서 찾아보니, gradle에서 간혹 엔티티 인식을 persistenc.xml 파일에 명시해주어야 할 경우가 있는 것 같다. 하지만, 이는 순수 jpa에서만 주의해주면 되는 것 같다

> reference: https://www.inflearn.com/questions/17098

/META-INF/persistence.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">  
    <persistence-unit name="jpabook">  
        <class>com.example.ch06jpastart15.domain.entity.Member</class>  
        <class>com.example.ch06jpastart15.domain.entity.MemberProduct</class>  
        <class>com.example.ch06jpastart15.domain.entity.Product</class>  
        <properties>  
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>  
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>  
            <property name="javax.persistence.jdbc.user" value="sa"/>  
            <property name="javax.persistence.jdbc.password" value=""/>  
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>  
              
            <property name="hibernate.show_sql" value="true"/>  
            <property name="hibernate.format_sql" value="true"/>  
            <property name="hibernate.hbm2ddl.auto" value="create"/>  
            <property name="hibernate.ejb.naming_strategy" value="org.hibernate.cfg.ImprovedNamingStrategy"/>  
            <property name="hibernate.id.new_generator_mappings" value="true"/>  
        </properties>  
    </persistence-unit>  
</persistence>
```

