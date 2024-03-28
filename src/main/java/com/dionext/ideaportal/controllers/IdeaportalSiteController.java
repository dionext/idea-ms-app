package com.dionext.ideaportal.controllers;


import com.dionext.ideaportal.services.*;
import com.dionext.site.controllers.BaseSiteController;
import com.dionext.site.dto.SearchWrapper;
import com.dionext.site.services.PageParserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Ideaportal Site Controller", description = "Ideaportal Site Controller")
@RequestMapping(value = {"/ideaportal"})
public class IdeaportalSiteController extends BaseSiteController {
    private IdeaportalPageCreatorService ideaportalPageElementService;
    private AuthorCreatorService authorCreatorService;
    private TopicCreatorService topicCreatorService;
    private CiteCreatorService citeCreatorService;
    private BibliographyCreatorService bibliographyCreatorService;
    private PageParserService pageParserService;
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

    //https://stackoverflow.com/questions/17955777/redirect-to-an-external-url-from-controller-action-in-spring-mvc
    @GetMapping(value = {"/app/index.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppIndex() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create("../index.htm")).build();
    }

    @GetMapping(value = {"/app/aut.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppAut() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create("../author/list")).build();
    }

    @PostMapping(value = {"/author/search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchWrapper> processAuthorSearch(HttpServletRequest request, @RequestParam Map<String, String> parameters) {
        log.debug("search..." + request.getQueryString());
        return authorCreatorService.searchAuthor(parameters.get("term"));
    }

    @GetMapping(value = {"/author/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processAuthor(HttpServletRequest request) {
        return sendOk(authorCreatorService.createPage());
    }

    @GetMapping(value = {"/topic/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processTopic(HttpServletRequest request) {
        return sendOk(topicCreatorService.createPage());
    }

    @GetMapping(value = {"/bibliography/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processBibliography(HttpServletRequest request) {
        return sendOk(bibliographyCreatorService.createPage());
    }

    @GetMapping(value = {"/cite/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processCite(HttpServletRequest request) {
        return sendOk(citeCreatorService.createPage());
    }

    @GetMapping(value = {"/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processPage() {
        return sendOk(
                createSimpleSitePage(pageParserService, ideaportalPageElementService));
    }

}
