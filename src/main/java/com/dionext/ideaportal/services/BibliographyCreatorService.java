package com.dionext.ideaportal.services;

import com.dionext.ideaportal.db.entity.Bibliography;
import com.dionext.ideaportal.db.repositories.BibliographyRepository;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.exceptions.DioRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BibliographyCreatorService extends IdeaportalPageCreatorService {

    private static int padSize = 1000;

    private BibliographyRepository bibliographyRepository;

    @Autowired
    public void setBibliographyRepository(BibliographyRepository bibliographyRepository) {
        this.bibliographyRepository = bibliographyRepository;
    }


    public String createPage() {
        pageInfo.addPageTitle(i18n.getString("ideaportal.menu.aphorisms.sources"));

        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize, Sort.by("id"));
            Page<Bibliography> listPage = bibliographyRepository.findAll(pageable);
            listPage.getTotalElements();

            body.append("<ul>");
            for (Bibliography item : listPage) {
                body.append("<li>");
                body.append(makeItemBlock(item, false));
                body.append("</li>");
            }
            body.append("</ul>");

        } else {

            Bibliography item = bibliographyRepository.findById(pageInfo.getId()).orElse(null);
            if (item != null) {
                body.append(makeItemBlock(item, true));
            } else {
                throw new DioRuntimeException();//to do
            }
        }

        return createHtmlAll(new SrcPageContent(body.toString()));

    }

    private String makeItemBlock(Bibliography bibliography, boolean singlePage) {
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            //str.append("""
            //        <a href="
            //        """);
            //str.append(bibliography.getId());
            //str.append("""
            //        ">
            //        """);
            str.append(bibliography.getName());
            //str.append("""
            //        </a>
            //        """);

        } else {
            str.append("<br/>");
            str.append(bibliography.getName());
            str.append("<br/>");
        }
        return str.toString();
    }
}
