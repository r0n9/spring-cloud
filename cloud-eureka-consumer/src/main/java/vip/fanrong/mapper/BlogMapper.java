package vip.fanrong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.Blog;
import vip.fanrong.model.User;

import java.util.List;

/**
 * Created by Rong on 2018/1/10.
 */
@Mapper
@Repository
public interface BlogMapper {

    //通过博客id找到博客
    Blog getBlogById(@Param("id") Long id);

    //通过一定条件查找博客
    List<Blog> getBlogs(@Param("userId") Long userId, @Param("tag") String tag);

    //创建一篇新的博客
    void createBlog(@Param("blog") Blog blog);

    //修改一篇博客
    void updateBlog(Blog blog);

    //通过id删除一篇博客
    void deleteBlog(@Param("id") Long id);
}
