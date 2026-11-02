package com.coworking.hotdesk.rowmapper;

import com.coworking.hotdesk.entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        User user = new User();

        user.setUserId(rs.getLong("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setWalletBalance(rs.getBigDecimal("wallet_balance"));

        return user;
    }
}