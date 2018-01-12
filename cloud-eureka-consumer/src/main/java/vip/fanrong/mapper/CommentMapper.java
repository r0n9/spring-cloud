package vip.fanrong.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import vip.fanrong.model.Comment;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {

    public void createComment(@Param("blogId") Long blogId, Comment comment);

    public void deleteComment(@Param("id") Long commentId);

    public List<Comment> getCommentsByBlogId(@Param("id") Long id);
}
