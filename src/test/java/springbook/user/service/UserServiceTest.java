package springbook.user.service;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

    List<User> users;

    @Autowired
    ApplicationContext context;
    private UserDaoJdbc userDao;
    @Autowired
    UserService userService;
//    @Autowired
//    DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

//    @Test
//    void bean(){
//        assertThat(this.userService).isNotNull();
//    }

    @BeforeEach
    void setUp(){
        users = Arrays.asList(
                new User("leenclair", "링클레어", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joy", "조이", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("spring", "스프링", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
                new User("toby", "토비", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("young", "김영한", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
        this.userDao = this.context.getBean("userDao", UserDaoJdbc.class);
    }

    @Test
    void upgradeLevels() throws Exception {
        userDao.deleteAll();

        for(User user : users) userDao.add(user);

//        userService.upgradeLevels();
//        userService.upgradeLevelsV2();
        userService.upgradeLevelsV4();

//        checkLevel(users.get(0), Level.BASIC);
//        checkLevel(users.get(1), Level.SILVER);
//        checkLevel(users.get(2), Level.SILVER);
//        checkLevel(users.get(3), Level.GOLD);
//        checkLevel(users.get(4), Level.GOLD);
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    @Test
    void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
    }

    @Test
    public void upgradeAllOrNothing() throws Exception{
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
//        testUserService.setDataSource(this.dataSource);
        testUserService.setTransactionManager(transactionManager);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
//            testUserService.upgradeLevelsV2();
//            testUserService.upgradeLevelsV3();
            testUserService.upgradeLevelsV4();
            fail("TestUserServiceException expected");
        }catch(TestUserServiceException e){

        }

        checkLevelUpgraded(users.get(1), false);
    }

    static class TestUserService extends UserService{
        private String id;

        private TestUserService(String id){
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException{

    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }

    private void checkLevelUpgraded(User user, boolean upgraded){
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        }
        else{
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }


}
