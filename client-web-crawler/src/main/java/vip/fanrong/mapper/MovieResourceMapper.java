package vip.fanrong.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.MovieResource;


/**
 * Created by Rong on 2017/12/11.
 */
@Mapper
@Repository
public interface MovieResourceMapper {

    @InsertProvider(type = MovieResourceProvider.class, method = "insert")
    int insert(@Param("movieResource") MovieResource movieResource);

}
