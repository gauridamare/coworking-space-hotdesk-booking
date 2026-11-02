package com.coworking.hotdesk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "hotdesk.console.enabled=false")
class HotdeskApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void databaseConnectionWorks() {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM workspaces",
                Integer.class
        );

        assertNotNull(count);
        assertTrue(count > 0);

        System.out.println("MySQL connection successful!");
        System.out.println("Total workspaces: " + count);
    }
}