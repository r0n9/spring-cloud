package vip.fanrong.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.KdsTopic;

import java.util.List;

/**
 * Created by Rong on 2017/12/18.
 */
@Mapper
@Repository
public interface KdsTopicMapper {

    @InsertProvider(type = KdsTopicProvider.class, method = "batchInsert")
    int batchInsert(@Param("kdsTopicList") List<KdsTopic> kdsTopicList);

}
