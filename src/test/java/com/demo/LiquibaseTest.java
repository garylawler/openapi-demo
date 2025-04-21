package com.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = {LiquibaseTest.Initializer.class})
class LiquibaseTest {

    @ClassRule
    public static MySQLContainer mySQLContainer =
            new MySQLContainer("mysql:8.0.27")
                    .withDatabaseName("test-db")
                    .withUsername("sa")
                    .withPassword("sa");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            mySQLContainer.start();
            TestPropertyValues.of(
                            "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                            "spring.datasource.username=" + mySQLContainer.getUsername(),
                            "spring.datasource.password=" + mySQLContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
            jdbcTemplate =
                    new JdbcTemplate(
                            DataSourceBuilder.create()
                                    .url(mySQLContainer.getJdbcUrl())
                                    .username(mySQLContainer.getUsername())
                                    .password(mySQLContainer.getPassword())
                                    .build());
        }
    }

    private static JdbcTemplate jdbcTemplate;

    @Test
    void changeLog() {
        jdbcTemplate.query(
                "SELECT * FROM DATABASECHANGELOG",
                rs -> {
                    assertThat(rs.getString(3))
                            .isEqualTo("/db/migrations/001-validate-db-creation.sql");
                });
    }

    @Test
    void tableCreated() {
        jdbcTemplate.execute("SELECT * FROM TEST");
    }

    @Test
    void invalidTableNotFound() {
        assertThrows(
                BadSqlGrammarException.class, () -> jdbcTemplate.execute("SELECT * FROM TEST2"));
    }
}
