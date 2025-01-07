package com.dionext.ideaportal.controllers;


import com.dionext.ideaportal.services.*;
import com.dionext.site.controllers.BaseSiteController;
import com.dionext.site.dto.SearchWrapper;
import com.dionext.site.services.PageParserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Ideaportal Site Controller", description = "Ideaportal Site Controller")
@RequestMapping(value = {"/"})
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
        return sendOk(citeCreatorService.createPage(false));
    }
    @GetMapping(value = {"/cite-favorite/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processCiteFavorite(HttpServletRequest request) {
        return sendOk(citeCreatorService.createPage(true));
    }
    @GetMapping(value = {"/proetcontra/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processCiteProetcontra(HttpServletRequest request) {
        return sendOk(citeCreatorService.createPageProetcontra(true));
    }

    //for text pages
    @GetMapping(value = {"/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> processPage() {
        return sendOk(
                createSimpleSitePage(pageParserService, ideaportalPageElementService));
    }

    //////////////////////////////////////////////////
    ////legacy
    //https://stackoverflow.com/questions/17955777/redirect-to-an-external-url-from-controller-action-in-spring-mvc
    protected String removePath(int level, String path) {
        level = level - 1;// -context
        for (int i = 0; i < level; i++) {
            int index = path.lastIndexOf("/");
            if (index > -1)
                path = path.substring(0, index);
        }
        log.debug("Path after revove: " + path);
        return path;
    }

    @GetMapping(value = {"/app/index.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppIndex(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestParam Map<String, String> parameters)  {
        return redirect("index.htm");
    }
    @GetMapping(value = {"/app/aut.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppAut(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam Map<String, String> parameters) {
        return redirect("author/list");
    }
    @GetMapping(value = {"/app/aut_s_{id}.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppAutS(HttpServletRequest request,
                                              HttpServletResponse response,
                                               @PathVariable String id) {
        return redirect("author/" + id);
    }
    @GetMapping(value = {"/app/lit.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppLit(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam Map<String, String> parameters) {
        return redirect("bibliography/list");
    }
    @GetMapping(value = {"/app/tem.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppTem(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam Map<String, String> parameters) {
        return redirect("topic/list");
    }
    @GetMapping(value = {"/app/proe.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppProe(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam Map<String, String> parameters) {
        return redirect("proetcontra/list");
    }
    @GetMapping(value = {"/app/fil.htm"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processAppFil(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @RequestParam Map<String, String> parameters) {
        return redirect("cite-favorite/list");
    }
    @GetMapping(value = {"/app/**", "/app//**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processOtherLegacy(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestParam Map<String, String> parameters)  {
        return redirect("index.htm");
    }
    @GetMapping(value = {"/index.php", "/component/**",
            "/kontakty", "/izmenit-lichnye-dannye", "/katalog-ssylok"})
    public ResponseEntity<Void> processIndexPhp(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestParam Map<String, String> parameters)  {
        return redirect("index.htm");
    }

    @GetMapping(value = {
            "/razdely",
            "/razdely/russkaya-ideya",
            "/razdely/filosofiya",
            "/razdely/filosofiya/filosofskaya-aforistika",
            "/razdely/ideologiya",
            "/razdely/",
            "/razdely/russkaya-ideya/",
            "/razdely/filosofiya/",
            "/razdely/filosofiya/filosofskaya-aforistika/",
            "/razdely/ideologiya/"
    }, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> processLegacyAtricles(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestParam Map<String, String> parameters)  {
        return redirect("index.htm");
    }
    private ResponseEntity<Void> redirect(String redirectRelAddres) {
        String newUrl = pageInfo.getDomainUrl() +"/"+ redirectRelAddres;
        //log.debug("newUrl: " + newUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(
                URI.create(newUrl)).build();
    }

}
