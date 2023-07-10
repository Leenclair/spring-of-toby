package springbook.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import springbook.user.domain.User;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserDaoTest {

    @Test
    void add() throws SQLException, ClassNotFoundException {
        UserDao userDao = new UserDao();

        User user = new User();
        user.setId("leenclair");
        user.setName("링클레어");
        user.setPassword("test");

        userDao.add(user);

        assertThat(user.getId()).isEqualTo("leenclair");
        assertThat(user.getName()).isEqualTo("링클레어");
        assertThat(user.getPassword()).isEqualTo("test");

//        log.info(user.getId() + " 등록성공");
        User user2 = userDao.get(user.getId());
        assertThat(user2.getId()).isEqualTo("leenclair");
//        log.info(user2.getName());
//        log.info(user2.getPassword());
//        log.info(user2.getId() + " 조회 성공");
    }

}