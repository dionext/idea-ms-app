package com.dionext.ideaportal.services;


import com.dionext.site.services.PageCreatorService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Primary
@SuppressWarnings({"java:S5663"})
public class IdeaportalPageCreatorService extends PageCreatorService {


    @Override
    public String createBodyTopBanner() {

        return MessageFormat.format("""
                <div class="page-header container">
                   <h1 class="text-page-header">{0}</h1>
                </div>""", pageInfo.getSiteTitle());
    }

    @Override
    public String createBodyTopMenuStyle() {
        return """
                <nav class="navbar navbar-expand-lg navbar-light">""";
    }


}
