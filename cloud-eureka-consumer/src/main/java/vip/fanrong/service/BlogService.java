package vip.fanrong.service;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import vip.fanrong.exception.NotFoundException;
import vip.fanrong.mapper.BlogMapper;
import vip.fanrong.mapper.TagMapper;
import vip.fanrong.model.Blog;
import vip.fanrong.model.Comment;
import vip.fanrong.model.Tag;
import vip.fanrong.util.TagUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    private BlogMapper blogMapper;
    private TagMapper tagMapper;
    private CacheService cacheService;
    private ZSetOperations zSetOperations;

    @Autowired
    private CommentService commentService;

    @Autowired
    public BlogService(RedisTemplate redisTemplate, BlogMapper blogMapper, TagMapper tagMapper, CacheService cacheService) {
        this.blogMapper = blogMapper;
        this.tagMapper = tagMapper;
        this.cacheService = cacheService;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void createBlog(Blog blog, String tags) {
        blogMapper.createBlog(blog);
        TagUtils.setBlogTags(blog, tags);
    }


    public List<Blog> showBlogs() {
        List<Blog> blogs = blogMapper.getBlogs(null, null);
        for (Blog blog : blogs) {
            blog.setContent(Jsoup.parse(blog.getContent()).text());
            blog.setComments(commentService.getCommentsByBlogId(blog.getId()));
        }
        return blogs;
    }

    public List<Blog> getBlogsByTag(String tag) {
        List<Blog> blogs = blogMapper.getBlogs(null, tag);
        for (Blog blog : blogs) {
            blog.setContent(Jsoup.parse(blog.getContent()).text());
            blog.setComments(commentService.getCommentsByBlogId(blog.getId()));
        }
        return blogs;
    }

    // 获取前20名热门博客（4篇为一页）
    public List<Blog> getHotBlogs(int page) {
        Set<String> hotBlogs = zSetOperations.range("hotBlogsRank", (page - 1) * 4, page * 4 - 1);

        List<Blog> blogs = new ArrayList<>();

        if (hotBlogs == null || hotBlogs.isEmpty()) {
            // 如果缓存里没有
            List<Blog> allBlogs = blogMapper.getBlogs(null, null);
            for (int i = (page - 1) * 4; i < page * 4 - 1; i++) {
                if (allBlogs.size() > i) {
                    blogs.add(allBlogs.get(i));
                }
            }
            return blogs;
        }

        for (String id : hotBlogs) {
            Blog blog = null;
            try {
                blog = quickGetBlog(Long.parseLong(id));
            } catch (NumberFormatException | NotFoundException e) {
                continue;
            }
            blog.setContent(Jsoup.parse(blog.getContent()).text());
            blogs.add(blog);
        }
        return blogs;
    }

    private Blog quickGetBlog(Long id) throws NotFoundException {
        // 从redis里获取博客数据
        String blogDetails = cacheService.getFromRedis("blogId:" + id);
        if (blogDetails == null) {
            // 缓存里没有，从数据库里得到数据，根据浏览次数排名来判断是否要缓存
            logger.debug("从数据库里读取Blog");
            Blog blog = blogMapper.getBlogById(id);
            // 找不到这篇博客，404
            if (blog == null) {
                throw new NotFoundException("blog not found");
            }

            blog.setComments(commentService.getCommentsByBlogId(id));

            Long rank = zSetOperations.rank("hotBlogsRank", "" + id);
            if (rank != null && rank <= 20) {
                // 排名在前二十，要缓存，转换成json
                Gson gson = new Gson();
                cacheService.setToRedis("blogId:" + id, gson.toJson(blog));
            }
            return blog;
        } else {
            logger.debug("从redis里读取Blog");
            // 缓存里有，直接取出
            Gson gson = new Gson();
            Blog blog = gson.fromJson(blogDetails, Blog.class);
            return blog;
        }
    }

    public List<Blog> showUserBlogs(Long userId) {
        return blogMapper.getBlogs(userId, null);
    }

    public Blog getBlogForBrowse(Long id) throws NotFoundException {
        // 增加阅读量
        // 按照hit从小到大排列，hit减1
        cacheService.incrScore("hotBlogsRank", "" + id, -1);
        return quickGetBlog(id);
    }

    public void deleteBlog(Long id) {
        blogMapper.deleteBlog(id);

        //清除缓存
        cacheService.delFromRedis("blogId:" + id);
        zSetOperations.remove("hotBlogsRank", "" + id);
        Set<Tag> tags = tagMapper.findByBlog(id);
        for (Tag tag : tags) {
            cacheService.incrScore("tags", tag.getName(), 1);
        }

        zSetOperations.removeRangeByScore("tags", 0, 2);

        tagMapper.deleteByBlog(id);
    }

    public Blog getBlogForEdit(Long id) throws NotFoundException {
        return quickGetBlog(id);
    }

    public Blog updateBlog(Long id, Blog form, String tags) {

        // 找到要修改的博客
        Blog blog = blogMapper.getBlogById(id);

        // 更新标题和内容
        blog.setTitle(form.getTitle());
        blog.setContent(form.getContent());

        // 更新博客
        blogMapper.updateBlog(blog);
        // 更新标签
        blog = TagUtils.setBlogTags(blog, tags);

        // 清除缓存
        cacheService.delFromRedis("blogId:" + id);

        return blog;
    }

}
