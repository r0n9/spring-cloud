package vip.fanrong.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.ZmzAccount;

@Mapper
@Repository
public interface ZmzAccountMapper {

    @Insert("insert into zmz_account " +
            "(email, nickname, password, sex, lastlogindate, registerdate, isvalid) " +
            "values " +
            "(#{email}, #{nickname}, #{password}, #{sex}, #{lastLoginDate}, #{registerDate}, #{isValide})")
    @Options(useGeneratedKeys = true)
    int insert(ZmzAccount zmzAccount); // 返回自增长id
}
