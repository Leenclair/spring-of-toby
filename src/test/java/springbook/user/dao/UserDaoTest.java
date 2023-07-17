package springbook.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class UserDaoTest {

    @Test
    void add() throws SQLException {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao userDao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("leenclair");
        user.setName("링클레어");
        user.setPassword("test");

        userDao.add(user);

        assertThat(user.getId()).isEqualTo("leenclair");
        assertThat(user.getName()).isEqualTo("링클레어");
        assertThat(user.getPassword()).isEqualTo("test");

        User user2 = userDao.get(user.getId());
        assertThat(user2.getId()).isEqualTo("leenclair");
    }
}