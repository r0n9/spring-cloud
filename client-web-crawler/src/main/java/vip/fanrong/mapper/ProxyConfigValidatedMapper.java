package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.ProxyConfig;

import java.util.List;

/**
 * Created by Rong on 2017/12/4.
 */
@Mapper
@Repository
public interface ProxyConfigValidatedMapper {

    @Insert("insert into proxy_config_validated " +
            "(host, port, location, type, status, statusUpdateTime, insertTime) " +
            "values " +
            "(#{host}, #{port}, #{location}, #{type}, #{status}, #{statusUpdateTime}, #{insertTime})")
    @Options(useGeneratedKeys = true)
    int insert(ProxyConfig proxyConfig); // 返回自增长id

    @Select("select * from proxy_config_validated where status = 200 order by id desc limit #{limit}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "host", property = "host"),
            @Result(column = "port", property = "port"),
            @Result(column = "location", property = "location"),
            @Result(column = "type", property = "type"),
            @Result(column = "status", property = "status"),
            @Result(column = "statusUpdateTime", property = "statusUpdateTime"),
            @Result(column = "insertTime", property = "insertTime")
    })
    List<ProxyConfig> getValidatedProxyConfigsByLimit(@Param("limit") int limit);

    @Select("select * from proxy_config_validated where status = 200 and type = #{type} order by id desc limit #{limit}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "host", property = "host"),
            @Result(column = "port", property = "port"),
            @Result(column = "location", property = "location"),
            @Result(column = "type", property = "type"),
            @Result(column = "status", property = "status"),
            @Result(column = "statusUpdateTime", property = "statusUpdateTime"),
            @Result(column = "insertTime", property = "insertTime")
    })
    List<ProxyConfig> getValidatedProxyConfigsByLimit(@Param("limit") int limit, @Param("type") String type);

    @Delete("delete from proxy_config_validated where id = #{id}")
    int delete(@Param("id") int id);
}
