package com.gym.crm.health;

import com.gym.crm.dao.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DemoUsersHealthIndicator implements HealthIndicator {

    private final UserDao userDao;
    private final List<String> requiredUsernames;

    public DemoUsersHealthIndicator(
            UserDao userDao,
            @Value("${health.demo.required-users:John.Smith,Sarah.Williams}") String requiredUsersCsv
    ) {
        this.userDao = userDao;
        this.requiredUsernames = Arrays.stream(requiredUsersCsv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    @Override
    public Health health() {
        try {
            List<String> missingUsers = requiredUsernames.stream()
                    .map(username -> {
                        Optional<?> user = userDao.findByUsername(username);
                        return user.isPresent() ? null : username;
                    })
                    .filter(value -> value != null)
                    .toList();

            if (!missingUsers.isEmpty()) {
                return Health.down()
                        .withDetail("reason", "Required demo users are missing")
                        .withDetail("requiredUsers", requiredUsernames)
                        .withDetail("missingUsers", missingUsers)
                        .build();
            }

            return Health.up()
                    .withDetail("requiredUsers", requiredUsernames)
                    .withDetail("checkedUsersCount", requiredUsernames.size())
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Cannot validate required demo users")
                    .build();
        }
    }
}
