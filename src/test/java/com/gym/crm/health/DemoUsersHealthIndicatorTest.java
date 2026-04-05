package com.gym.crm.health;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemoUsersHealthIndicatorTest {

    @Mock
    private UserDao userDao;

    @Test
    void health_ReturnsUp_WhenAllRequiredUsersExist() {
        when(userDao.findByUsername("John.Smith")).thenReturn(Optional.of(new User()));
        when(userDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(new User()));

        DemoUsersHealthIndicator indicator = new DemoUsersHealthIndicator(userDao, "John.Smith,Sarah.Williams");
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("checkedUsersCount"));
    }

    @Test
    void health_ReturnsDown_WhenOneRequiredUserIsMissing() {
        when(userDao.findByUsername("John.Smith")).thenReturn(Optional.of(new User()));
        when(userDao.findByUsername("Sarah.Williams")).thenReturn(Optional.empty());

        DemoUsersHealthIndicator indicator = new DemoUsersHealthIndicator(userDao, "John.Smith,Sarah.Williams");
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        List<?> missingUsers = (List<?>) health.getDetails().get("missingUsers");
        assertTrue(missingUsers.contains("Sarah.Williams"));
    }
}
