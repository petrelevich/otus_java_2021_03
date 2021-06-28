package ru.otus.connectionpool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;


public class DemoPool {
    private static final Logger logger = LoggerFactory.getLogger(DemoPool.class);
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private DataSource dataSourcePool;

    public static void main(String[] args) throws SQLException {
        var demoPool = new DemoPool();
        demoPool.flywayMigrations();
        demoPool.useConnectionPool();
    }

    private DemoPool() {
        createConnectionPool();
    }

    private void createConnectionPool() {
        var config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setConnectionTimeout(3000); //ms
        config.setIdleTimeout(60000); //ms
        config.setMaxLifetime(600000);//ms
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setUsername("usr");
        config.setPassword("pwd");

        dataSourcePool = new HikariDataSource(config);
    }

    private void useConnectionPool() throws SQLException {
        try (var connection = dataSourcePool.getConnection();
             var pst = connection.prepareStatement("select count(*) as counter from test")) {
            try (var rs = pst.executeQuery()) {
                if (rs.next()) {
                    logger.info("counter: {}", rs.getString("counter"));
                }
            }
            connection.rollback();
        }
    }

    private void flywayMigrations() {
        logger.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSourcePool)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        logger.info("db migration finished.");
        logger.info("***");
    }
}
