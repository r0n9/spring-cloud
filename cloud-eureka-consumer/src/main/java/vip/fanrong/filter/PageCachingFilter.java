package vip.fanrong.filter;

import net.sf.ehcache.constructs.web.filter.SimpleCachingHeadersPageCachingFilter;

public class PageCachingFilter extends SimpleCachingHeadersPageCachingFilter {

    private final String customCacheName;

    public PageCachingFilter(String name) {
        this.customCacheName = name;
    }

    @Override
    protected String getCacheName() {
        return customCacheName;
    }

}