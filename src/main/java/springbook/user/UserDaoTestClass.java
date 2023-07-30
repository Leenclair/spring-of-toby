package springbook.user;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTestClass {

    public static void main(String[] args) throws SQLException {

//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        ApplicationContext context = new GenericXmlApplicationContext("test-applicationContext.xml");
        UserDaoJdbc userDao = context.getBean("userDao", UserDaoJdbc.class);

        User user = new User();
        user.setId("링클레어");
        user.setId("leenclair");
        user.setName("링클레어");
        user.setPassword("test");

        userDao.add(user);

        System.out.println(user.getId() + " 등록성공");

        User user2 = userDao.get(user.getId());
//        System.out.println("user name = " + user2.getName());
//        System.out.println("user password = " + user2.getPassword());
//        System.out.println(user2.getId() + " 조회성공");
        if(!user.getName().equals(user2.getName())){
            System.out.println("테스트 실패 (name)");
        }else if(!user.getPassword().equals(user2.getPassword())){
            System.out.println("테스트 실패 (password)");
        }else{
            System.out.println("조회 테스트 성공");
        }

//        DaoFactory factory = new DaoFactory();
//        UserDao dao1 = factory.userDao();
//        UserDao dao2 = factory.userDao();
//        System.out.println("dao1 = " + dao1);
//        System.out.println("dao2 = " + dao2);
//        UserDao dao3 = context.getBean("userDao", UserDao.class);
//        UserDao dao4 = context.getBean("userDao", UserDao.class);
//        System.out.println("dao3 = " + dao3);
//        System.out.println("dao4 = " + dao4);
    }
}
