package ru.otus.mappedby;

import ru.otus.core.HibernateUtils;
import ru.otus.mappedby.models.Avatar;
import ru.otus.mappedby.models.Email;
import ru.otus.mappedby.models.Person;

public class MappedByDemo {
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        HibernateUtils.buildSessionFactory(HIBERNATE_CFG_FILE, Avatar.class, Email.class, Person.class);
    }
}
