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

    @Autowired
    private CacheService cacheService;

    //得到一篇博客的所有评论
    public List<Comment> getCommentsByBlogId(Long id) {
        return commentMapper.getCommentsByBlogId(id);
    }

    @Async
    public void createComment(Long blogId, Comment comment) {
        // 删除DB记录
        commentMapper.createComment(blogId, comment);
        // 清除缓存
        cacheService.delFromRedis("blogId:" + blogId);
    }

    @Async
    public void deleteComment(Comment comment) {
        // 删除DB记录
        commentMapper.deleteComment(comment.getCommentId());
        // 清除缓存
        cacheService.delFromRedis("blogId:" + comment.getBlogId());
    }
}
