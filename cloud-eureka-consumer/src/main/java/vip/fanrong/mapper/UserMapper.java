package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.User;

/*
 * 用户的增删改查
 */
@Mapper
@Repository
public interface UserMapper {

    //通过id或邮箱找到用户
    public User getUser(@Param("id") Long id, @Param("email") String email);

    //更新用户信息
    public void updateUser(User user);

    //创建一个用户
    public void createUser(User user);

    //删除一个用户
    public void deleteUser(@Param("id") long id);
}
