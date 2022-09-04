package com.example.jpql.test;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        Session session = em.unwrap(Session.class);

        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                String sql = "select * from MEMBER";
                Statement statement = connection.createStatement();
                statement.execute(sql);
                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    long memberId = resultSet.getLong(1);
                    String name = resultSet.getString(2);

                    System.out.println("member: " + memberId + " " + name);
                }
            }
        });
    }
}
