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
    public String createBodyTopMenuSignIn() {
        StringBuilder str = new StringBuilder();
        str.append("""
                <ul class="nav navbar-nav navbar-right">
                    <li class="active">
                """);
        if (SecurityUtils.isLoggedIn()){
            str.append(MessageFormat.format("""
                    <img src="/images/user.svg" width="30" height="30" class="d-inline-block align-top" title="{1}">
                    <a href="/login?logout">{0}</a>
                """, i18n.getString("menu.logout"),
                    SecurityUtils.isUserInAdminRole()?i18n.getString("menu.signed.in.admin") : i18n.getString("menu.signed.in.user") ));
        }
        else {
            str.append(MessageFormat.format("""
                    <a href="/login">{0}</a>
                """, i18n.getString("menu.sign.in")));
        }
        str.append("""
                    </li>
                 </ul>                
                """);
        return str.toString();
    }

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
