package com.dionext.ideaportal.services;

import com.dionext.configuration.CacheConfiguration;
import com.dionext.site.dto.PageUrl;
import com.dionext.site.services.PageParserService;
import com.dionext.site.services.SitemapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class IdeaportalSitemapService {
    SitemapService sitemapService;
    private IdeaportalPageCreatorService ideaportalPageElementService;

    private AuthorCreatorService authorCreatorService;
    private TopicCreatorService topicCreatorService;
    private CiteCreatorService citeCreatorService;
    private BibliographyCreatorService bibliographyCreatorService;
    private PageParserService pageParserService;
    @Autowired
    public void setSitemapService(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }
    @Autowired
    public void setPageParserService(PageParserService pageParserService) {
        this.pageParserService = pageParserService;
    }

    @Autowired
    public void setIdeaportalPageElementService(IdeaportalPageCreatorService ideaportalPageElementService) {
        this.ideaportalPageElementService = ideaportalPageElementService;
    }

    @Autowired
    public void setAuthorCreatorService(AuthorCreatorService authorCreatorService) {
        this.authorCreatorService = authorCreatorService;
    }

    @Autowired
    public void setTopicCreatorService(TopicCreatorService topicCreatorService) {
        this.topicCreatorService = topicCreatorService;
    }

    @Autowired
    public void setCiteCreatorService(CiteCreatorService citeCreatorService) {
        this.citeCreatorService = citeCreatorService;
    }

    @Autowired
    public void setBibliographyCreatorService(BibliographyCreatorService bibliographyCreatorService) {
        this.bibliographyCreatorService = bibliographyCreatorService;
    }

    @Cacheable(CacheConfiguration.CACHE_COMMON)
    public String createSitemap(boolean langSupport) {
        log.debug("creating sitemap");
        List<PageUrl> pages = new ArrayList<>();
        pages.addAll(ideaportalPageElementService.findAllPages());
        pages.addAll(topicCreatorService.findAllTopicPages());
        pages.add(new PageUrl("proetcontra/list"));
        pages.addAll(authorCreatorService.findAllAuthorPages());

        return sitemapService.createSitemap(pages, ideaportalPageElementService, langSupport);
    }


}
