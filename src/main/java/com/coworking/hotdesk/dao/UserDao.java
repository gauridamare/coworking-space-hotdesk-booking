
package com.coworking.hotdesk.dao;
import java.math.BigDecimal;
import com.coworking.hotdesk.entity.User;
import com.coworking.hotdesk.rowmapper.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get user by ID
    public User getUserById(Long userId) {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                new UserRowMapper(),
                userId
        );
    }

    // Register a new user
    public int addUser(User user) {

        String sql = "INSERT INTO users "
                + "(full_name, email, wallet_balance) "
                + "VALUES (?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                user.getFullName(),
                user.getEmail(),
                user.getWalletBalance()
        );
    }

    // Update user wallet balance
   
public int updateWalletBalance(Long userId, BigDecimal balance) {

    String sql = "UPDATE users "
            + "SET wallet_balance = ? WHERE user_id = ?";

    return jdbcTemplate.update(sql, balance, userId);
}

}
