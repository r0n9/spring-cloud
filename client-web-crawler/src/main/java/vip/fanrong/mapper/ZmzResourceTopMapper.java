package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.MovieResource;
import vip.fanrong.model.ZmzResourceTop;

import java.util.List;

/**
 * Created by Rong on 2017/11/29.
 */
@Mapper
@Repository
public interface ZmzResourceTopMapper {

    @Insert("insert into zmz_resource_top " +
            "(get_time, count, type, src, img_data_src, " +
            "name, name_en, processed, process_time) " +
            "values " +
            "(#{getTime}, #{count}, #{type}, #{src}, #{imgDataSrc}, " +
            "#{name}, #{nameEn}, #{processed}, #{processTime})")
    @Options(useGeneratedKeys = true)
    int insert(ZmzResourceTop zmzResourceTop); // 返回自增长id

    @InsertProvider(type = ZmzResourceTopProvider.class, method = "batchInsert")
    int batchInsert(@Param("zmzResourceTopList") List<ZmzResourceTop> zmzResourceTopList);


    @Select("select * from zmz_resource_top_current")
    @Results({
            @Result(column = "get_time", property = "getTime"),
            @Result(column = "count", property = "count"),
            @Result(column = "type", property = "type"),
            @Result(column = "src", property = "src"),
            @Result(column = "img_data_src", property = "imgDataSrc"),
            @Result(column = "name", property = "name"),
            @Result(column = "name_en", property = "nameEn"),
            @Result(column = "processed", property = "processed"),
            @Result(column = "process_time", property = "processTime")
    })
    List<ZmzResourceTop> selectLatest();
}
