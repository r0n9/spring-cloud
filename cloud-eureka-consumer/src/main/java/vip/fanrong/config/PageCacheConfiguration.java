package vip.fanrong.config;

import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.fanrong.filter.ClearPageCachingFilter;
import vip.fanrong.filter.PageCachingFilter;

@Configuration
@AutoConfigureAfter(CacheConfiguration.class)
public class PageCacheConfiguration {

    // 页面缓存
    @Bean
    public FilterRegistrationBean registerBlogsPageFilter() {
        PageCachingFilter customPageCachingFilter = new PageCachingFilter("vip.fanrong.mapper.index");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customPageCachingFilter);
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/kds"));
        return filterRegistrationBean;
    }

    // 清除页面缓存
    @Bean
    public FilterRegistrationBean registerClearBlogsPageFilter() {
        ClearPageCachingFilter clearPageCachingFilter = new ClearPageCachingFilter("vip.fanrong.mapper.index");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(clearPageCachingFilter);
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/update", "/blogs/update"));
        return filterRegistrationBean;
    }

}