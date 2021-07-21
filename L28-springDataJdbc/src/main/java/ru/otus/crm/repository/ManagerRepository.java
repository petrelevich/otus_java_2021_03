package ru.otus.crm.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Manager;

import java.util.List;


public interface ManagerRepository extends CrudRepository<Manager, String> {

    // закоментируйте, чтобы получить N+1
    @Override
    @Query(value = """
            select m.id    as manager_id,
                   m.label as manager_label,
                   c.id    as client_id,
                   c.name  as client_name
            from manager m
                     left outer join client c
                                     on m.id = c.manager_id
            order by m.id
                                                          """,
            resultSetExtractorClass = ManagerResultSetExtractorClass.class)
    List<Manager> findAll();
}
