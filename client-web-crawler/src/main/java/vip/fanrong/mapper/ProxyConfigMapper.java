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
public interface ProxyConfigMapper {

    @Insert("insert into proxy_config " +
            "(host, port, location, type, status, statusUpdateTime, insertTime) " +
            "values " +
            "(#{host}, #{port}, #{location}, #{type}, #{status}, #{statusUpdateTime}, #{insertTime})")
    @Options(useGeneratedKeys = true)
    int insert(ProxyConfig proxyConfig); // 返回自增长id

    @InsertProvider(type = ProxyConfigProvider.class, method = "batchInsert")
    int batchInsert(@Param("proxyConfigList") List<ProxyConfig> proxyConfigList);

    @Update("update proxy_config set host = #{host}, port = #{port}, location = #{location},  type = #{type}, " +
            "status = #{status}, statusUpdateTime = #{statusUpdateTime}, insertTime = #{insertTime} where id = #{id}")
    int update(ProxyConfig proxyConfig);

    @Select("select * from proxy_config where status is null order by id desc limit #{limit};")
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
    List<ProxyConfig> getProxyConfigsByLimit(@Param("limit") int limit);


}
