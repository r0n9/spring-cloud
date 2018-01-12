package vip.fanrong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vip.fanrong.mapper.CommentMapper;
import vip.fanrong.model.Comment;

import java.util.List;

@Component
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    //得到一篇博客的所有评论
    public List<Comment> getCommentsByBlogId(Long id) {
        return commentMapper.getCommentsByBlogId(id);
    }

    @Async
    public void createComment(Long blogId, Comment comment) {
        commentMapper.createComment(blogId, comment);
    }

    @Async
    public void deleteComment(Comment comment) {
        commentMapper.deleteComment(comment.getCommentId());
    }
}
