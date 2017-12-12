package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.MovieResource;

import java.util.List;


/**
 * Created by Rong on 2017/12/11.
 */
@Mapper
@Repository
public interface MovieResourceMapper {

    @InsertProvider(type = MovieResourceProvider.class, method = "insert")
    int insert(@Param("movieResource") MovieResource movieResource);


    @Select("select * from movie_resource where source = #{source} and resource_id = #{resourceId};")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "source", property = "source"),
            @Result(column = "resource_id", property = "resourceId"),
            @Result(column = "name", property = "name"),
            @Result(column = "alt_name", property = "altName"),
            @Result(column = "file_name", property = "fileName"),
            @Result(column = "file_size", property = "fileSize"),
            @Result(column = "download_type", property = "downloadType"),
            @Result(column = "download_link", property = "downloadLink"),
            @Result(column = "insert_time", property = "insertTime")
    })
    List<MovieResource.MovieResourceFile> selectBySourceAndResourceId(@Param("source")String source, @Param("resourceId")String resourceId);

}
