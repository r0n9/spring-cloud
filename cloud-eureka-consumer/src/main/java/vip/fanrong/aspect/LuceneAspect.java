package vip.fanrong.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import vip.fanrong.model.Blog;
import vip.fanrong.util.LuceneUtils;

import java.io.IOException;

@Aspect
@Component
public class LuceneAspect {

    @Pointcut("execution(* vip.fanrong.service.BlogService.createBlog(..))")
    private void createPointCut() {
    }

    @Pointcut("execution(* vip.fanrong.service.BlogService.updateBlog(..))")
    private void updatePointCut() {
    }

    @Pointcut("execution(* vip.fanrong.service.BlogService.deleteBlog(..))")
    private void deletePointCut() {
    }

    @AfterReturning(pointcut = "createPointCut()", returning = "blog") //1
    public void createIndex(Blog blog) {
        if (blog != null) {
            try {
                LuceneUtils.createIndex(blog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(pointcut = "updatePointCut()", returning = "blog") //1
    public void updateIndex(Blog blog) {
        if (blog != null) {
            try {
                LuceneUtils.updateIndex(blog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(pointcut = "deletePointCut()", returning = "id") //1
    public void deleteIndex(Long id) {
        LuceneUtils.deleteIndex(id);
    }
}
