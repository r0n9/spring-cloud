package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.TvResource;

import java.util.List;


@Mapper
@Repository
public interface TvResourceMapper {

    @Insert("insert into tv_resource " +
            "(source, resource_id, name, alt_name, season, episode, toggle, " +
            "file_name, file_size, download_type, download_link, insert_time) " +
            "values " +
            "(#{source}, #{resourceId}, #{name}, #{altName}, #{season}, #{episode}, #{toggle}" +
            ", #{fileName}, #{fileSize}, #{downloadType}, #{downloadLink}, #{insertTime})")
    @Options(useGeneratedKeys = true)
    int insert(TvResource tvResource); // 返回自增长id


    @InsertProvider(type = TvResourceProvider.class, method = "batchInsert")
    int batchInsert(@Param("tvResourceList") List<TvResource> tvResourceList);

    @Select("select DISTINCT source, resource_id, season, episode, toggle from tv_resource " +
            "where source = #{source} and resource_id = #{resourceId};")
    @Results({
            @Result(column = "source", property = "source"),
            @Result(column = "resource_id", property = "resourceId"),
            @Result(column = "season", property = "season"),
            @Result(column = "episode", property = "episode"),
            @Result(column = "toggle", property = "toggle")
    })
    List<TvResource> selectResourceIndex(@Param("source") String source, @Param("resourceId") String resourceId);
}
