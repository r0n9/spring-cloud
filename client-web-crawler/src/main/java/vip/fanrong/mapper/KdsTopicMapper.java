package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
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


    @Select("select * from kds_topics_14;")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "title", property = "title"),
            @Result(column = "link", property = "link"),
            @Result(column = "img_link", property = "imgLink"),
            @Result(column = "reply", property = "replyTo"),
            @Result(column = "user", property = "userTo"),
            @Result(column = "post_time", property = "postTime"),
            @Result(column = "insert_time", property = "insertTime")
    })
    List<KdsTopic> selectLatest();

}
