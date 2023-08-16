package springbook.user.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

//    private DataSource dataSource;
//
//    public void setDataSource(DataSource dataSource){
//        this.dataSource = dataSource;
//    }
    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    //    public void upgradeLevels(){
//        List<User> users = userDao.getAll();
//
//        for(User user : users){
//            Boolean changed = null;
//            if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){
//                user.setLevel(Level.SILVER);
//                changed = true;
//            }
//            else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){
//                user.setLevel(Level.GOLD);
//                changed = true;
//            }
//            else if(user.getLevel() == Level.GOLD){changed = false;}
//            else{changed = false;}
//
//            if(changed) {userDao.update(user);}
//        }
//    }

//    public void upgradeLevelsV2() throws Exception{
//
//        TransactionSynchronizationManager.initSynchronization();
//        Connection c = DataSourceUtils.getConnection(dataSource);
//        c.setAutoCommit(false);
//
//        try {
//            List<User> users = userDao.getAll();
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            c.commit();
//        }catch (Exception e){
//            c.rollback();
//            throw e;
//        }finally {
//            DataSourceUtils.releaseConnection(c, dataSource);
//            TransactionSynchronizationManager.unbindResource(this.dataSource);
//            TransactionSynchronizationManager.clearSynchronization();
//        }
//    }

//    public void upgradeLevelsV3(){
//        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//
//        try{
//            List<User> users = userDao.getAll();
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            transactionManager.commit(status);
//        }catch (RuntimeException e){
//            transactionManager.rollback(status);
//            throw e;
//        }
//    }

    public void upgradeLevelsV4(){
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);
        }catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMailV3(user);
    }

    public void add(User user){
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    private void sendUpgradeEMailV1(User user){
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.ksug.org");
        Session s = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(s);
        try{
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("Upgrade 안내");
            message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");

            Transport.send(message);
        }catch (AddressException e){
            throw new RuntimeException(e);
        }catch (MessagingException e){
            throw new RuntimeException(e);
        }
//        catch (UnsupportedEncodingException e){
//            throw new RuntimeException(e);
//        }
    }

    private void sendUpgradeEMailV2(User user){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.server.com");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");

        mailSender.send(mailMessage);
    }

    private void sendUpgradeEMailV3(User user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");

        this.mailSender.send(mailMessage);
    }
}
