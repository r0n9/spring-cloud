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

    // 通过id找到用户
    @Select("select * from user where id = #{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "email", property = "email"),
            @Result(column = "description", property = "description"),
            @Result(column = "avatar", property = "avatar"),
            @Result(column = "insert_time", property = "insertTime"),
            @Result(column = "invited", property = "invited")
    })
    User getUserById(@Param("id") Long id);

    // 通过邮箱找到用户
    @Select("select * from user where email = #{email}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "email", property = "email"),
            @Result(column = "description", property = "description"),
            @Result(column = "avatar", property = "avatar"),
            @Result(column = "insert_time", property = "insertTime"),
            @Result(column = "invited", property = "invited")
    })
    User getUserByEmail(@Param("email") String email);

    // 更新用户信息
    @Update("update user set username = #{username}, password = #{password}, email = #{email}, " +
            "description = #{description}, avatar = #{avatar}, invited = #{invited} where id = #{id}")
    int updateUser(User user);

    // 创建一个用户
    @Insert("insert into user " +
            "(username, password, email, description, avatar, insert_time, invited) " +
            "values " +
            "(#{username}, #{password}, #{email}, #{description}, #{avatar}, #{insertTime}, #{invited})")
    @Options(useGeneratedKeys = true)
    long createUser(User user);

    // TODO 删除一个用户
    void deleteUser(@Param("id") long id);
}
