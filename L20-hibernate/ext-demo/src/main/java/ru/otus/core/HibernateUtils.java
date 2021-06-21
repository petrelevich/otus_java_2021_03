package ru.otus.core;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Arrays;

public final class HibernateUtils {

    private HibernateUtils() {
    }

    public static SessionFactory buildSessionFactory(Configuration configuration, Class<?>... annotatedClasses) {
        MetadataSources metadataSources = new MetadataSources(createServiceRegistry(configuration));
        Arrays.stream(annotatedClasses).forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static SessionFactory buildSessionFactory(String configResourceName, Class<?>... annotatedClasses) {
        Configuration configuration = new Configuration().configure(configResourceName);
        return buildSessionFactory(configuration, annotatedClasses);
    }

    private static StandardServiceRegistry createServiceRegistry(Configuration configuration) {
        return new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
    }
}
