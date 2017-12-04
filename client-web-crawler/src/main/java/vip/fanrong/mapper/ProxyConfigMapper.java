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
}
