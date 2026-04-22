package com.example.demo.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;

@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("EntityManagerFactory is not a Hibernate EntityManagerFactory");
        }
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

    public SessionFactory createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            configuration.setProperty("hibernate.connection.username", "root");
            configuration.setProperty("hibernate.connection.password", "123456");
            
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            
            configuration.addAnnotatedClass(com.example.demo.model.Patient.class);
            configuration.addAnnotatedClass(com.example.demo.model.Prescription.class);
            configuration.addAnnotatedClass(com.example.demo.model.PrescriptionDetail.class);
            configuration.addAnnotatedClass(com.example.demo.model.Medicine.class);
            
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create SessionFactory: " + e.getMessage());
        }
    }
}
