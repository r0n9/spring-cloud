package vip.fanrong.filter;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import javax.servlet.*;
import java.io.IOException;


public class ClearPageCachingFilter implements Filter {

    private final CacheManager cacheManager;

    private final String customCacheName;

    public ClearPageCachingFilter(String name) {
        this.customCacheName = name;
        cacheManager = CacheManager.getInstance();
        assert cacheManager != null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    //暂时先简单地把缓存全部删光
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        Ehcache ehcache = cacheManager.getEhcache(customCacheName);
        ehcache.removeAll();
    }

    @Override
    public void destroy() {
    }

}
