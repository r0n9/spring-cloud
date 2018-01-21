package vip.fanrong.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import net.minidev.json.JSONUtil;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import vip.fanrong.Constant;
import vip.fanrong.client.SearchServiceClient;
import vip.fanrong.exception.NotFoundException;
import vip.fanrong.mapper.BlogMapper;
import vip.fanrong.mapper.TagMapper;
import vip.fanrong.model.Blog;
import vip.fanrong.model.Comment;
import vip.fanrong.model.Tag;
import vip.fanrong.util.TagUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
        createIndex(blog);
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

        searchServiceClient.deleteIndex(Constant.INDEX_KEY, String.valueOf(id));

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

        // index
        updateIndex(blog);

        return blog;
    }


    @Autowired
    private SearchServiceClient searchServiceClient;

    public void createIndex(Blog blog) {
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES)); // 对标题做索引
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));// 对文章内容做索引
        searchServiceClient.createIndex(Constant.INDEX_KEY, blog.getId() + "", fromLuceneDoc(doc));
    }

    public void updateIndex(Blog blog) {
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));
        searchServiceClient.updateIndex(Constant.INDEX_KEY, blog.getId() + "", fromLuceneDoc(doc));
    }

    public List<Blog> search(String keyword, int pageStart, int pageSize) {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("title");
        fields.add("content");
        ObjectNode blogsNode = searchServiceClient.search(Constant.INDEX_KEY, keyword, fields, pageStart, pageSize);

        String status = blogsNode.get("status").asText();

        if ("success".equalsIgnoreCase(status)) {
            JsonNode results = blogsNode.get("results");
            if (results == null) return null;
            List<Blog> blogs = new LinkedList<>();

            for (JsonNode result : results) {
                String id = result.get("id").asText();
                String title = result.get("title").asText();
                String content = result.get("content").asText();
                Blog blog = new Blog();
                blog.setId(Long.parseLong(id));
                blog.setTitle(title);
                blog.setContent(content);
                blogs.add(blog);
            }
            return blogs;
        }

        return null;
    }


    public Document toLuceneDoc(String id, vip.fanrong.model.Document doc) {
        Document docLucene = new Document();
        docLucene.add(new TextField("id", id, Field.Store.YES));
        for (vip.fanrong.model.Document.TextField textField : doc.getTextFields()) {
            if ("id".equalsIgnoreCase(textField.getName())) {
                continue;
            }
            docLucene.add(new TextField(textField.getName(), textField.getValue(), textField.getStore() ? Field.Store.YES : Field.Store.NO));
        }
        return docLucene;
    }

    public vip.fanrong.model.Document fromLuceneDoc(Document docLucene) {
        vip.fanrong.model.Document doc = new vip.fanrong.model.Document();
        List<vip.fanrong.model.Document.TextField> fields = new ArrayList<>();
        doc.setTextFields(fields);
        docLucene.forEach(f -> fields.add(new vip.fanrong.model.Document.TextField(f.name(), f.stringValue(), true)));
        return doc;
    }
}
