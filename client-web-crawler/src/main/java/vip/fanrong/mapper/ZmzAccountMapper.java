package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.ZmzAccount;

import java.util.List;

@Mapper
@Repository
public interface ZmzAccountMapper {

    @Insert("insert into zmz_account " +
            "(email, nickname, password, sex, lastlogindate, registerdate, isvalid) " +
            "values " +
            "(#{email}, #{nickname}, #{password}, #{sex}, #{lastLoginDate}, #{registerDate}, #{isValide})")
    @Options(useGeneratedKeys = true)
    int insert(ZmzAccount zmzAccount); // 返回自增长id

    @Select("select * from zmz_account where isvalid = 1;")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "email", property = "email"),
            @Result(column = "nickname", property = "nickname"),
            @Result(column = "password", property = "password"),
            @Result(column = "sex", property = "sex"),
            @Result(column = "lastlogindate", property = "lastLoginDate"),
            @Result(column = "registerdate", property = "registerDate"),
            @Result(column = "isvalid", property = "isValide")
    })
    List<ZmzAccount> getZmzAccountAll();

    @Update("update zmz_account set email = #{email}, nickname = #{nickname}, password = #{password},  sex = #{sex}, " +
            "lastlogindate = #{lastLoginDate}, registerdate = #{registerDate}, isvalid = #{isValide} where id = #{id}")
    int update(ZmzAccount zmzAccount);


}
