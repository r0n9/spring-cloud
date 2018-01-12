package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.User;

/**
 * Created by Rong on 2018/1/11.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testGetUser() {
        User user = userService.getUserByEmail("fanrong330@gmail.com");
        System.out.println(user);


        user = userService.getUserById(3l);
        System.out.println(user);
    }
}
