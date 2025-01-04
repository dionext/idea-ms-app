package com.dionext.ideaportal.services;

import com.dionext.ideaportal.db.entity.Author;
import com.dionext.ideaportal.db.entity.Cite;
import com.dionext.ideaportal.db.entity.Proe;
import com.dionext.ideaportal.db.entity.Topic;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import com.dionext.ideaportal.db.repositories.ProeRepository;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.exceptions.DioRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collection;

@Service
@SuppressWarnings({"java:S1192"})
public class CiteCreatorService extends IdeaportalPageCreatorService {

    private static int padSize = 100;
    private CiteRepository citeRepository;
    private ProeRepository proeRepository;

    @Autowired
    public void setCiteRepository(CiteRepository citeRepository) {
        this.citeRepository = citeRepository;
    }

    @Autowired
    public void setProeRepository(ProeRepository proeRepository) {
        this.proeRepository = proeRepository;
    }

    public String createPageProetcontra(boolean favorite) {
        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize, Sort.by("id"));
            Page<Proe> listPage = proeRepository.findAll(pageable);
            int allPagesCount = listPage.getTotalPages();
            String paginationStr = createPagination(pageInfo, allPagesCount);
            body.append(paginationStr);

            body.append(makeProeCiteList(listPage));

            body.append(paginationStr);
        } else {
            Cite item = citeRepository.findById(pageInfo.getId()).orElse(null);
            if (item != null) {
                body.append(makeCiteBlock(item, true));
            } else {
                throw new DioRuntimeException();//to do
            }
        }
        return createHtmlAll(new SrcPageContent(body.toString()));

    }
    public String createPage(boolean favorite) {
        if (favorite)
            pageInfo.addPageTitle(i18n.getString("ideaportal.menu.aphorisms.favorite"));

        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize, Sort.by("id"));
            Page<Cite> listPage = null;
            if (favorite)
                listPage = citeRepository.findAllFavorite(pageable);
            else
                listPage = citeRepository.findAll(pageable);
            int allPagesCount = listPage.getTotalPages();

            String paginationStr = createPagination(pageInfo, allPagesCount);
            body.append(paginationStr);

            body.append(makeCiteList(listPage));

            body.append(paginationStr);
        } else {
            throw new DioRuntimeException();//to do
        }
        return createHtmlAll(new SrcPageContent(body.toString()));

    }
    public String makeCiteListByAuthor(String authorId) {
        StringBuilder body = new StringBuilder();
        Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize);
        Page<Cite> citeList = citeRepository.findByAuthorId(authorId, pageable);
        int allPagesCount = citeList.getTotalPages();
        String paginationStr = createPagination(authorId, pageInfo.getSiteSettings().getListPageDelimiter(), pageInfo.getExtension(), pageInfo.getPageNum(), allPagesCount);
        body.append("""
                <div><h3>Цитаты автора</h3><p>Найдено цитат автора: 
                """);
        body.append(citeList.getTotalElements());
        body.append("""
                </p></div>
                """);
        body.append(paginationStr);
        body.append(makeCiteList(citeList));
        body.append(paginationStr);
        return body.toString();
    }

    //public String makeCiteListByTopic(String topicId) {
    //    StringBuilder body = new StringBuilder();
    //    Collection<Cite> citeList = citeRepository.findByTopics_Id(topicId);
    //    body.append(makeCiteList(citeList));
    //    return body.toString();
    //}
    public String makeCiteListByTopicNative(Topic topic) {
        Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize);
        StringBuilder body = new StringBuilder();
        Page<Cite> citeList = citeRepository.findByTopicHcodeNative(topic.getHcode().length(), topic.getHcode(), pageable);
        int allPagesCount = citeList.getTotalPages();
        String paginationStr = createPagination(topic.getId(), pageInfo.getSiteSettings().getListPageDelimiter(), pageInfo.getExtension(), pageInfo.getPageNum(), allPagesCount);
        body.append("""
                <p>Найдено цитат по теме: 
                """);
        body.append(citeList.getTotalElements());
        body.append("""
                </p>
                """);
        body.append(paginationStr);
        body.append(makeCiteList(citeList));
        body.append(paginationStr);
        return body.toString();
    }
    private String makeCiteList(Iterable<Cite> citeList) {
        StringBuilder body = new StringBuilder();
        for (Cite item : citeList) {
            body.append(makeCiteBlock(item, false));
            body.append("<br/>");
        }
        return body.toString();
    }
    private String makeProeCiteList(Iterable<Proe> citeList) {
        pageInfo.addPageTitle(i18n.getString("ideaportal.menu.aphorisms.proetcontra"));
        StringBuilder body = new StringBuilder();
        body.append("""
            <div class="container">
            """);
        for (Proe item : citeList) {
            body.append("""
                    <div class="row">
                    """);

            body.append("""
                    <div class="col">
                            """);
            body.append(MessageFormat.format("""
                    <div class="text-center mb-4 pb-2">
                        <img src="{0}"
                        alt="yin and yang" >
                    </div>
                    """, pageInfo.getOffsetStringToContextLevel() + "images/proet1.gif"));
            body.append(makeCiteBlock(item.getProCite(), false));
            body.append("</div>");

            body.append("""
                    <div class="col">
                            """);
            body.append(MessageFormat.format("""
                    <div class="text-center mb-4 pb-2">
                        <img src="{0}"
                        alt="yin and yang">
                    </div>
                    """, pageInfo.getOffsetStringToContextLevel() + "images/proet.gif"));
            body.append(makeCiteBlock(item.getContraCite(), false));
            body.append("</div>");

            body.append("</div>");
            body.append("<br/>");
        }
        body.append("</div>");
        return body.toString();
    }

    private String makeCiteBlock(Cite item, boolean singlePage) {
        Author author = item.getAuthor();
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            str.append("""
                    <div class="card">
                      <div class="card-body">
                        <p class="card-text">
                                        """);
            str.append(item.getText());
            if (author != null) {
                str.append("""
                        <p align="right">
                        """);
                str.append("""
                        <a href="
                        """);
                str.append("../author/" + author.getId());
                str.append("""
                        ">
                        """);
                str.append(author.getNamep());
                str.append("""
                        </a>
                        """);
                str.append("</p>");
            }
            str.append("""
                        </p>
                      </div>
                    </div>                    """);

        } else {
            str.append("<br/>");
            str.append(item.getText());
            str.append("<br/>");
        }
        return str.toString();
    }
}
