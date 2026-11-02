
package com.coworking.hotdesk.service;
import java.math.BigDecimal;
import com.coworking.hotdesk.dao.UserDao;
import com.coworking.hotdesk.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Get user by ID
    public User getUserById(Long userId) {
        return userDao.getUserById(userId);
    }

    // Register a new user
    public void addUser(User user) {
        userDao.addUser(user);
    }

    // Update wallet balance
    public void updateWalletBalance(Long userId, BigDecimal balance) {
    userDao.updateWalletBalance(userId, balance);
}
}
