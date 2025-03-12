package com.dionext.ideaportal.services;

import com.dionext.ai.entity.AiModel;
import com.dionext.ai.entity.AiPrompt;
import com.dionext.ai.entity.AiRequest;
import com.dionext.ai.repositories.AiModelRepository;
import com.dionext.ai.repositories.AiPromptRepository;
import com.dionext.ai.repositories.AiRequestRepository;
import com.dionext.ai.services.AIRequestService;
import com.dionext.ideaportal.db.entity.*;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import com.dionext.ideaportal.db.repositories.ProeRepository;
import com.dionext.libauthspringstarter.com.dionext.security.services.SecurityUtils;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.exceptions.DioRuntimeException;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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

    @Autowired
    AiRequestRepository aiRequestRepository;
    @Autowired
    private AiModelRepository aiModelRepository;
    @Autowired
    private AiPromptRepository aiPromptRepository;

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
            Cite item = citeRepository.findById(Integer.valueOf(pageInfo.getId())).orElse(null);
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
            Cite item = citeRepository.findById(Integer.valueOf(pageInfo.getId())).orElse(null);
            if (item != null) {
                //this.pageInfo.addPageTitle(author.getNamep());
                body.append( makeCiteBlock(item, true));
            } else {
                throw new DioRuntimeException();
            }
        }
        return createHtmlAll(new SrcPageContent(body.toString()));

    }
    public String makeCiteListByAuthor(Integer authorId) {
        StringBuilder body = new StringBuilder();
        Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize);
        Page<Cite> citeList = citeRepository.findByAuthorId(authorId, pageable);
        int allPagesCount = citeList.getTotalPages();
        String paginationStr = createPagination(String.valueOf(authorId), pageInfo.getSiteSettings().getListPageDelimiter(), pageInfo.getExtension(), pageInfo.getPageNum(), allPagesCount);
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
        String paginationStr = createPagination(String.valueOf(topic.getId()), pageInfo.getSiteSettings().getListPageDelimiter(), pageInfo.getExtension(), pageInfo.getPageNum(), allPagesCount);
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
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            str.append("""
                    <div class="card">
                      <div class="card-body">
                      """);
            str.append(createCiteTextInnerBlock(item));
            createAuthorPBlock(item, str);
            createAIExplorationBlock(item, str);
            createInfoHref(item, str);
            str.append("""
                        </p>
                      </div>
                    </div>                    """);
        } else {
            str.append("<br/>");
            str.append(createCiteTextInnerBlock(item));
            createAuthorPBlock(item, str);
            createBibliographyBlock(item, str);
            createAIExplorationBlock(item, str);
            str.append("<br/>");
        }
        return str.toString();
    }
    private static void createInfoHref(Cite item, StringBuilder str) {
        str.append("""
                    <p align="left">
                    """);
        str.append("""
                    <a href="
                    """);
        str.append("../cite/" + item.getId());
        str.append("""
                    ">
                    """);
        str.append("Доп. информация по афоризму");
        str.append("""
                    </a>
                    """);
        str.append("</p>");

    }

    private static void createAuthorPBlock(Cite item, StringBuilder str) {

        Author author = item.getAuthor();
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
    }
    private static void createBibliographyBlock(Cite item, StringBuilder str) {

        Bibliography bibliography = item.getBibliography();
        String page = item.getPage();
        createBibliographyItem(str, bibliography, page);
        Bibliography bibliographyAlt = item.getBibliographyAlt();
        String pageAlt = item.getPageAlt();
        createBibliographyItem(str, bibliographyAlt, pageAlt);
    }

    private static void createBibliographyItem(StringBuilder str, Bibliography bibliography, String page) {
        if (bibliography != null) {
            str.append("""
                    <p align="left">
                    """);
            str.append("""
                    <a href="
                    """);
            str.append("../bibliography/" + bibliography.getId());
            str.append("""
                    ">
                    """);
            str.append("Источник: ");
            str.append(bibliography.getName());
            if (!Strings.isNullOrEmpty(page))
                str.append(" cтр. " + page);
            str.append("""
                    </a>
                    """);
            str.append("</p>");
        }
    }

    private void createAIExplorationBlock(Cite item, StringBuilder str) {
        if (true){
            if (item.getInfo() != null) {
                str.append("""
                    <div class="container bg-light" >
                    """);
                str.append("<p><i><b>Oбъяснение афоризма: </b></i></p>");
                str.append("<i>");
                str.append(item.getInfo());
                str.append("</i>");
                str.append("""
                    </div>
                    """);
            }
        }
        else {
            Collection<AiRequest> aiRequests = aiRequestRepository.findByExternalDomainAndExternalEntityAndExternalVariantAndExternalId(
                    Cite.IDEA, Cite.CITE, Cite.CITE_EXP, item.getId().toString());
            if (!aiRequests.isEmpty()) {
                str.append("<p><i><b>Oбъяснение афоризма: </b></i></p>");
                int i = 1;
                for (AiRequest aiRequest : aiRequests) {
                    str.append("<hr/>");
                    createAIInfoBlock(str, aiRequest, i);
                    str.append("<i>" + aiRequest.getResult().replace("\n", "<br/>") + "</i>");
                    i++;
                }
            }
        }
    }

    private void createAIInfoBlock(StringBuilder str, AiRequest aiRequest, int number) {
        str.append("<p><i><b>");
        str.append("Вариант " + number + ": ");
        try {
            AiModel aiModel = aiModelRepository.findById(aiRequest.getAiModelId()).orElse(null);
            AiPrompt aiPrompt = aiPromptRepository.findById(aiRequest.getAiPromptId()).orElse(null);
            str.append(aiModel.getProvider());
            str.append(" ");
            str.append(aiModel.getModel() + "(id:" + aiModel.getId() + ")");
            str.append(" ");
            str.append("Prompt: \"" + aiPrompt.getName() + "\"" + "(id:" + aiPrompt.getId() + ")" );
            if (aiRequest.getDuration() != null)
                str.append(" Time: " + ((double) aiRequest.getDuration().longValue()) / 1000.0 + "s");
            str.append(" Cost: " + String.format("%.6f", AIRequestService.calculateCost(aiModel, aiRequest)) + "$"
                + " (" +String.format("%.6f", aiRequest.getCost()) + "$)");
        }
        catch (Exception ex){
            str.append("---");
        }
        str.append("</b></i></p>");
    }

    public String createCiteTextInnerBlock(Cite item) {
        StringBuilder str = new StringBuilder();
        str.append(MessageFormat.format("""
                   <div  id="cite-text-{0}">
                   <p class="card-text">
                                        """
                , item.getId()));
        str.append(item.getText());
        str.append("""
                        </p>
                """);
        if (SecurityUtils.isUserInAdminRole()) {
            str.append(MessageFormat.format("""
                    <div hx-target="#cite-text-{0}" hx-swap="innerHTML">
                        <button hx-get="/admin/cite/edit-text/{0}" type="button" class="btn btn-light">
                        Редактировать
                        </button>
                    </div>
                    """, item.getId()));
        }
        str.append("""
                        </div>
                """);
        return str.toString();
    }

    public String editCiteText(String id, String text) {
        Cite item = citeRepository.findById(Integer.valueOf(id)).orElse(null);
        if (item != null) {
            if (text != null && !text.equals(item.getText())) {
                item.setText(text);
                citeRepository.save(item);
            }
            return createCiteTextInnerBlock(item);
        }
        else throw new DioRuntimeException("Item not found for id " + id);
    }
    public void prepareCiteText(String id, Model model) {
        Cite item = citeRepository.findById(Integer.valueOf(id)).orElse(null);
        model.addAttribute("item", item);
    }

}
