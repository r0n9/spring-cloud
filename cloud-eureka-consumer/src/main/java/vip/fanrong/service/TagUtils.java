package vip.fanrong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.mapper.TagMapper;
import vip.fanrong.model.Blog;
import vip.fanrong.model.Tag;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagUtils {

    private static TagMapper tagMapper;
    private static CacheService cacheService;

    @Autowired
    public void setTagDao(TagMapper tagMapper) {
        TagUtils.tagMapper = tagMapper;
    }

    @Autowired
    public void setCacheService(CacheService cacheService) {
        TagUtils.cacheService = cacheService;
    }

    //得到String类型的tag
    public static String toStringTags(Set<Tag> tags) {
        StringBuilder tagStringBuilder = new StringBuilder();
        boolean first = true;
        for (Tag tag : tags) {
            if (first) {
                tagStringBuilder.append(tag.getName());
                first = false;
            } else {
                tagStringBuilder.append(",").append(tag.getName());
            }
        }
        return tagStringBuilder.toString();
    }

    //得到Set类型的tag
    public static Set<Tag> toSetTags(String tags) {
        String[] tagStr = tags.split(",");
        Set<Tag> tagList = new HashSet<>();
        for (String tag : tagStr) {
            tagList.add(new Tag(tag));
        }
        return tagList;
    }

    //给博客设置标签，并把设置好的博客返回
    public static Blog setBlogTags(Blog blog, String tags) {
        Set<Tag> oldTagsSet = blog.getTags();
        if (oldTagsSet == null || oldTagsSet.isEmpty()) {
            if (tags == null || tags.isEmpty()) {
                //本来标签为空，现在也为空
                return blog;
            }
        } else {
            //本来标签不为空，删除老的标签
            tagMapper.deleteByBlog(blog.getId());
            for (Tag tag : oldTagsSet) {
                cacheService.incrScore("tags", tag.getName(), 1);
            }
        }

        Set<Tag> newTagsSet = new HashSet<>();
        String[] newTags = tags.split(",");    //获取新的标签列表
        for (String tag : newTags) {
            if (tag.trim().isEmpty()) {
                continue;
            }
            //去重
            if (!newTagsSet.contains(new Tag(tag))) {
                newTagsSet.add(new Tag(tag));
                if (tagMapper.findOne(tag) == null) {
                    //数据库里没有的标签
                    tagMapper.create(tag);
                }
                tagMapper.addOneTagToBlog(tag, blog.getId());
            }
        }
        for (Tag tag : newTagsSet) {
            cacheService.incrScore("tags", tag.getName(), -1);
        }
        blog.setTags(newTagsSet);
        return blog;
    }

}
