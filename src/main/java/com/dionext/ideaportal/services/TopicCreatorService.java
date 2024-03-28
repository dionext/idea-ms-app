package com.dionext.ideaportal.services;


import com.dionext.ideaportal.db.entity.Topic;
import com.dionext.ideaportal.db.repositories.TopicRepository;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.exceptions.DioRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TopicCreatorService extends IdeaportalPageCreatorService {

    public static final int PAD_SIZE = 1000;
    TopicRepository topicRepository;

    @Autowired
    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public String createPage() {
        StringBuilder body = new StringBuilder();
        if (pageInfo.isList()) {
            Pageable pageable = PageRequest.of(pageInfo.getPageNum(), PAD_SIZE, Sort.by("hcode"));
            Page<Topic> listPage = topicRepository.findAll(pageable);
            listPage.getTotalElements();
            body.append("<ul>");
            int level = 1;
            for (Topic item : listPage) {
                int curLevel = item.getHcode().length();
                if (curLevel > level) {
                    body.append("<ul>");
                }
                if (curLevel < level) {
                    body.append("</ul>");
                }
                body.append("<li>");
                body.append(makeAutorBlock(item, false));
                body.append("</li>");
                level = curLevel;
            }
            body.append("</ul>");

        } else {


            Topic item = topicRepository.findById(pageInfo.getId()).orElse(null);
            if (item != null) {
                this.pageInfo.setPageTitle(item.getName());
                body.append(makeAutorBlock(item, true));
            } else {
                throw new DioRuntimeException();//to do
            }
        }


        return createHtmlAll(new SrcPageContent(body.toString()));

    }

    private String makeAutorBlock(Topic topic, boolean singlePage) {
        StringBuilder str = new StringBuilder();
        if (!singlePage) {
            str.append("""
                    <a href="
                    """);
            str.append(topic.getId());
            str.append("""
                    ">
                    """);
            str.append(topic.getName());
            str.append("""
                    </a>
                    """);

        } else {
            str.append("<br/>");
            str.append(topic.getName());
            str.append("<br/>");
        }
        return str.toString();
    }
}
