package ru.otus.hibernate;

import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.EntityStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.hibernate.model.Person;
import ru.otus.hibernate.model.Phone;
import java.util.ArrayList;

public class HiberDemo {
    private static final Logger logger = LoggerFactory.getLogger(HiberDemo.class);

    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";
    private final SessionFactory sessionFactory;

    public static void main(String[] args) {
        var demo = new HiberDemo();

        // demo.lifecycleDemo();
         demo.leakageDemo();

        // demo.fetchExample();
        // demo.jpqlExample();
        // demo.deleteFrom();
        // demo.nativeExample();
    }

    private void lifecycleDemo() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.getTransaction();
            transaction.begin();

            //Transient
            var person = new Person();
            person.setName("Ivan");
            person.setNickName("Petrov");
            person.setAddress("Lipovay str");

            //Managed
            session.persist(person);
            logger.info("persisted person:{}", person);

            var selected = session.load(Person.class, person.getId());
            logger.info("selected: {}", selected);
            logger.info(">>> updating >>>");
            logger.info("111 statistic. insertCount:{}, loadCount:{}",
                    getUsageStatistics().getInsertCount(), getUsageStatistics().getLoadCount());


            person.setAddress("moved street");
            transaction.commit();

            var updated = session.load(Person.class, person.getId());
            logger.info("updated: {}", updated);

            logger.info("222 statistic. insertCount:{}, loadCount:{}",
                    getUsageStatistics().getInsertCount(), getUsageStatistics().getLoadCount());

            var updatedAfterCommit = session.load(Person.class, person.getId());
            logger.info("updatedAfterCommit: {}", updatedAfterCommit);

            logger.info("333 statistic. insertCount:{}, loadCount:{}",
                    getUsageStatistics().getInsertCount(), getUsageStatistics().getLoadCount());

            //detached
            session.detach(updated);

            var personAfterDetached = session.load(Person.class, person.getId());
            logger.info("personAfterDetached: {}", personAfterDetached);

            logger.info("444 statistic. insertCount:{}, loadCount:{}",
                    getUsageStatistics().getInsertCount(), getUsageStatistics().getLoadCount());
        }
    }

    private void leakageDemo() {
        long createdPersonId;
        try (var session = sessionFactory.openSession()) {
            var transaction = session.getTransaction();
            transaction.begin();

            var person = new Person();
            person.setName("Ivan");
            person.setNickName("Sidorov");
            person.setAddress("Spiridonovka str");
            session.persist(person);
            logger.info("person:{}", person);

            transaction.commit();

            //session.detach(person);
            deepInIn(person);

            createdPersonId = person.getId();
            var selected = session.load(Person.class, createdPersonId);

            logger.info("selected: {}", selected);
        }

        try (var session = sessionFactory.openSession()) {
            var selected = session.load(Person.class, createdPersonId);
            logger.info("selected_2: {}", selected);
        }

    }

    //Далекая часть программы
    private void deepInIn(Person person) {
        person.setName("jon");
        logger.info("jon: {}", person);
    }

    private void fetchExample() {
        long personId;
        try (var session = sessionFactory.openSession()) {
            personId = createPerson(session);
        }
        Person selectedPerson;
        try (var session = sessionFactory.openSession()) {
            var selectedPhone = session.load(Phone.class, 3L);
            session.load(Phone.class, 3L);

            //selectedPhone фактически не загружен и select не сделан
            logger.info("111 statistic. insertCount:{}, loadCount:{}",
                    getUsageStatistics().getInsertCount(), getUsageStatistics().getLoadCount());
            logger.info("selectedPhone.class:{}", selectedPhone.getClass());

            logger.info("after phone load");
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>   selectedPhone: {}", selectedPhone);

            selectedPerson = session.load(Person.class, personId);
            logger.info("selectedPerson:{}", selectedPerson); //очень каварный вариант неявной инициализации

            //selectedPerson = session.get(Person.class, personId); //для НЕленивой загрузки

            logger.info(">>>>>>>>>>>>>>>>>>>>>>>   selectedPerson loaded");
            //} // сессия закрылась раньше, чеем мы воспользовались объектом.

            logger.info(">>>>>>>>>>>>>>>>>>>>>>>  selected person: {}", selectedPerson.getName());
            logger.info("phones:{}", selectedPerson.getPhones());
        }
    }


    private long createPerson(Session session) {
        var transaction = session.getTransaction();
        transaction.begin();

        var person = new Person();
        person.setName("Ivan");
        person.setNickName("Durak");
        person.setAddress("derv str");

        var listPhone = new ArrayList<Phone>();
        for (var idx = 0; idx < 5; idx++) {
            listPhone.add(new Phone("+" + idx, person));
        }
        person.setPhones(listPhone);
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>   persist...");
        session.save(person);
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>   commit...");

        // должны выполниться 1 insert для person и 5 для phone, update быть не должно
        transaction.commit();
        return person.getId();
    }

    private void jpqlExample() {
        long personId;
        try (var session = sessionFactory.openSession()) {
            personId = createPerson(session);
        }

        var entityManager = sessionFactory.createEntityManager();

        logger.info("select phone list:");

        var selectedPhones = entityManager.createQuery(
                "select p from Phone p where p.id > :paramId", Phone.class)
                .setParameter("paramId", 2L)
                .getResultList();

        logger.info("selectedPhones:{}", selectedPhones);


        var person = entityManager
                .createNamedQuery("get_person_by_id", Person.class)
                .setParameter("id", personId)
                .getSingleResult();

        logger.info("selected person:{}", person.getNickName());

        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(Person.class);
        var root = criteria.from(Person.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("id"), personId));

        var personCriteria = entityManager.createQuery(criteria).getSingleResult();
        logger.info("selected personCriteria:{}", personCriteria.getNickName());
        logger.info("selected personCriteria, Phones:{}", personCriteria.getPhones());
    }

    //https://www.baeldung.com/delete-with-hibernate
    //Deletion Using a JPQL Statement
    private void deleteFrom() {
        var person = new Person();
        person.setName("Ivan");
        person.setNickName("Durak");
        person.setAddress("derv str");

        try (var session = sessionFactory.openSession()) {
            var transaction = session.getTransaction();
            transaction.begin();
            session.save(person);
            transaction.commit();
        }

        var personId = person.getId();
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var loadedPerson = session.get(Person.class, personId); //загружаем в конекст, тут это важно
            logger.info("loadedPerson:{}", loadedPerson);

            var query = session.createQuery("delete from Person u where u.id = ?1");
            query.setParameter(1, personId);
            query.executeUpdate();

            var deletedPerson = session.get(Person.class, personId);
            logger.info("deletedPerson:{}", deletedPerson);

            session.getTransaction().commit();

            var reLoadedPerson = session.get(Person.class, personId);
            logger.info("reLoadedPerson:{}", reLoadedPerson);
        }
    }

    private void nativeExample() {
        long personId;
        try (var session = sessionFactory.openSession()) {
            personId = createPerson(session);
        }

        try (var session = sessionFactory.openSession()) {
            var name = session.doReturningWork(connection -> {
                try (var ps = connection.prepareStatement("select name from tPerson where id = ?")) {
                    ps.setLong(1, personId);
                    try (var rs = ps.executeQuery()) {
                        rs.next();
                        return rs.getString("name");
                    }
                }
            });
            logger.info("sqL name: {}", name);
        }
    }

    private HiberDemo() {
        flywayMigrations();

        var configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect")
                .setProperty("hibernate.connection.url", URL)
                .setProperty("hibernate.connection.username", USER)
                .setProperty("hibernate.connection.password", PASSWORD)

                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "validate") //validate - оптимальный вариант
                .setProperty("hibernate.generate_statistics", "true");

        var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        var metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Phone.class)
                .getMetadataBuilder()
                .build();

        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    private EntityStatistics getUsageStatistics() {
        var stats = sessionFactory.getStatistics();
        return stats.getEntityStatistics(Person.class.getName());
    }

    private void flywayMigrations() {
        logger.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(URL, USER, PASSWORD)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        logger.info("db migration finished.");
        logger.info("***");
    }
}

