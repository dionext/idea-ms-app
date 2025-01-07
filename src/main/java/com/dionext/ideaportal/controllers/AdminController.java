package com.dionext.ideaportal.controllers;


import com.dionext.site.controllers.BaseSiteController;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.site.services.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@Tag(name = "Admin Controller", description = "Admin Controller")
@RequestMapping(value = {"/admin"})
public class AdminController extends BaseSiteController {

    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("main")
    public ResponseEntity<String> getMainPage(@RequestParam Map<String, String> params) {
        return sendOk(adminService.createHtmlAll(new SrcPageContent(adminService.createAdminPage(params))));
    }

}
