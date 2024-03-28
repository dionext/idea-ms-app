package com.dionext.ideaportal.services;

import com.dionext.ideaportal.db.entity.Author;
import com.dionext.ideaportal.db.entity.Cite;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.exceptions.DioRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"java:S1192"})
public class CiteCreatorService extends IdeaportalPageCreatorService {

    private static int padSize = 100;
    private CiteRepository citeRepository;

    @Autowired
    public void setCiteRepository(CiteRepository citeRepository) {
        this.citeRepository = citeRepository;
    }

    public String createPage() {
        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), padSize, Sort.by("id"));
            Page<Cite> listPage = citeRepository.findAll(pageable);
            int allPagesCount = listPage.getTotalPages();
            listPage.getTotalElements();

            String paginationStr = createPagination(pageInfo, allPagesCount);
            body.append(paginationStr);

            for (Cite item : listPage) {
                body.append(makeAutorBlock(item, false));
                body.append("<br/>");
            }

            body.append(paginationStr);
        } else {
            Cite item = citeRepository.findById(pageInfo.getId()).orElse(null);
            if (item != null) {
                body.append(makeAutorBlock(item, true));
            } else {
                throw new DioRuntimeException();//to do
            }
        }


        return createHtmlAll(new SrcPageContent(body.toString()));

    }

    private String makeAutorBlock(Cite item, boolean singlePage) {
        Author author = item.getAuthor();
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            str.append("""
                    <div class="card">
                      <!--<img class="card-img-top" src="..." alt="Card image cap">-->
                      <div class="card-body">
                        <!--<h5 class="card-title">Card title</h5>-->
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
                str.append(author.getNames());
                str.append("""
                        </a>
                        """);
                str.append("</p>");
            }
            str.append("""
                        </p>
                        <!--<a href="#" class="btn btn-primary">Go somewhere</a>-->
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
