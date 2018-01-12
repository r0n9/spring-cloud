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
    public BlogService(RedisTemplate redisTemplate, BlogMapper blogMapper, TagMapper tagMapper, CacheService cacheService) {
        this.blogMapper = blogMapper;
        this.tagMapper = tagMapper;
        this.cacheService = cacheService;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void createBlog(Blog blog, String tags) {
        TagUtils.setBlogTags(blog, tags);
        blogMapper.createBlog(blog);
    }


    public List<Blog> showBlogs() {
        List<Blog> blogs = blogMapper.getBlogs(null, null);
        for (Blog blog : blogs) {
            blog.setContent(Jsoup.parse(blog.getContent()).text());
        }
        return blogs;
    }

    public List<Blog> getBlogsByTag(String tag) {
        List<Blog> blogs = blogMapper.getBlogs(null, tag);
        for (Blog blog : blogs) {
            blog.setContent(Jsoup.parse(blog.getContent()).text());
        }
        return blogs;
    }

    // 获取前十五名热门博客（3篇为一页）
    public List<Blog> getHotBlogs(int page) {
        Set<String> hotBlogs = zSetOperations.range("hotBlogsRank", (page - 1) * 3, page * 3 - 1);

        List<Blog> blogs = new ArrayList<>();

        if (hotBlogs == null || hotBlogs.isEmpty()) {
            // 如果缓存里没有
            List<Blog> allBlogs = blogMapper.getBlogs(null, null);
            for (int i = (page - 1) * 3; i < page * 3 - 1; i++) {
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
                e.printStackTrace();
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
}
