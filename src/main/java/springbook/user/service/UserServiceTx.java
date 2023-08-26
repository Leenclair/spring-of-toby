package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.domain.User;

public class UserServiceTx implements UserService{

    UserService userService;
    PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void add(User user) {
        this.userService.add(user);
    }

    public void upgradeLevelsV5() {
        TransactionStatus status = this.transactionManager.getTransaction((new DefaultTransactionDefinition()));
        try{
            userService.upgradeLevelsV5();

            this.transactionManager.commit(status);
        }catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
