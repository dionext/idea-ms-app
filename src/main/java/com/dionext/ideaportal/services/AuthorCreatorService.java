package com.dionext.ideaportal.services;

import com.dionext.ideaportal.db.entity.Author;
import com.dionext.ideaportal.db.repositories.AuthorRepository;
import com.dionext.site.dto.*;
import com.dionext.utils.Utils;
import com.dionext.utils.exceptions.DioRuntimeException;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings({"java:S1192", "java:S5663", "java:S5663"})
public class AuthorCreatorService extends IdeaportalPageCreatorService {


    private static int padSize = 100;

    private AuthorRepository authorRepository;

    private CiteCreatorService citeCreatorService;

    @Autowired
    public void setAuthorRepository(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    @Autowired
    public void setCiteCreatorService(CiteCreatorService citeCreatorService) {
        this.citeCreatorService = citeCreatorService;
    }


    @Override
    public String createBodyScripts() {
        return dfs(super.createBodyScripts()) +
                """
                         <script>
                          $( function() {
                            $( "#tags" ).autocomplete({
                               source:function(a,b){
                                   $.post(/*[[@{search}]]*/ "search", a, b)
                               },
                               minLength:2,
                               select:function(event, ui){
                                   window.location.replace(ui.item.id);
                               }
                            });
                          } );
                          </script>
                        """;
    }

    public String createPage() {
        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            pageInfo.addPageTitle(i18n.getString("ideaportal.menu.aphorisms.byauthor"));
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize, Sort.by("names"));
            Page<Author> listPage = authorRepository.findAll(pageable);
            int allPagesCount = listPage.getTotalPages();
            listPage.getTotalElements();

            String paginationStr = createPagination(pageInfo, allPagesCount);
            body.append(paginationStr);
            body.append("""
                    <div align="center" class="ui-widget">
                      <input id="tags">
                      <label for="tags"> поиск автора</label>
                    </div>
                    """);
            body.append("""
                    <ul>""");
            for (Author author : listPage) {
                body.append("<li>");
                body.append(makeAuthorBlock(author, false));
                body.append("</li>");
            }
            body.append("</ul>");

            body.append(paginationStr);
        } else {


            Author author = authorRepository.findById(Integer.valueOf(pageInfo.getId())).orElse(null);
            if (author != null) {
                this.pageInfo.addPageTitle(author.getNamep());
                body.append(makeAuthorBlock(author, true));
                body.append(citeCreatorService.makeCiteListByAuthor(author.getId()));
            } else {
                throw new DioRuntimeException();
            }
        }

        return createHtmlAll(new SrcPageContent(body.toString()));

    }

    private String makeAuthorBlock(Author author, boolean singlePage) {
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            str.append("""
                    <a href="
                    """);
            str.append(author.getId());
            str.append("""
                    ">
                    """);
            str.append(author.getNames());
            str.append("""
                    </a>
                    """);
            String yearFull = author.getYearFullStr();
            if (!Strings.isNullOrEmpty(yearFull)) {
                str.append(" (");
                str.append(author.getYearFullStr());
                str.append(") ");
            }
            if (!Strings.isNullOrEmpty(author.getInfo())) {
                str.append(" - ");
                str.append("<i>");
                str.append(Utils.restrictText(author.getInfo(), 300));
                str.append("</i>");
            }
        } else {
            if (!Strings.isNullOrEmpty(author.getPhoto())) {
                str.append("<div>");
                ImageDrawInfo img = new ImageDrawInfo();
                //img.setImagePath(pageInfo.getOffsetStringToContextLevel() + "images/foto/" + author.getPhoto());
                img.setImagePath("images/foto/" + author.getPhoto());
                str.append(createImage(img));
                str.append("</div>");
            }
            str.append("<b>");
            str.append(author.getNamep());
            str.append("</b>");
            str.append("<br/>");
            String yearFull = author.getYearFullStr();
            if (!Strings.isNullOrEmpty(yearFull)) {
                str.append("Годы жизни: ");
                str.append(author.getYearFullStr());
                str.append("<br/>");
            }
            if (!Strings.isNullOrEmpty(author.getInfo())) {
                str.append(author.getInfo());
                str.append("<br/>");
            }
        }
        return str.toString();
    }

    public List<SearchWrapper> searchAuthor(String term) {
        List<SearchWrapper> wrapperSearch = new ArrayList<>();
        for (Author item : authorRepository.findAllByTerm(term)) {
            wrapperSearch.add(new SearchWrapper(String.valueOf(item.getId()), item.getNames(), item.getNames()));
        }
        return wrapperSearch;
    }

    public List<PageUrl> findAllAuthorPages() {
        List<PageUrl> all = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, padSize, Sort.by("names"));
        Page<Author> listPage = authorRepository.findAll(pageable);
        int allPagesCount = listPage.getTotalPages();
        all.addAll(createPaginationForSitemap("author/" + pageInfo.getSiteSettings().getListPrefix(), pageInfo.getSiteSettings().getListPageDelimiter(),
                "htm",  allPagesCount));

        List<Author> list  = authorRepository.findAll();
        for (Author author : list){
            all.add(new PageUrl("author/" + author.getId()));
        }
        return all;
    }

}
