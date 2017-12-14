package vip.fanrong.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.TvResource;


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
}
