package com.dionext.ideaportal.controllers;

import com.dionext.ideaportal.services.CiteCreatorService;
import com.dionext.site.controllers.BaseSiteController;
import com.dionext.utils.exceptions.DioRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping(value = {"/admin"})
public class SimpleEditController extends BaseSiteController {

    @Autowired
    CiteCreatorService citeCreatorService;

    @GetMapping("cite/edit-text/{id}")
    public String editCite(@PathVariable String id, Model model) {
        citeCreatorService.prepareCiteText(id, model);
        return "cite :: edit";
    }

    @PostMapping("cite/update-text/{id}")
    public ResponseEntity<String> updateCiteText(@PathVariable String id,
                                 @RequestParam(value = "text") String text, @RequestParam(value = "action") String action,
                                 Model model) {
        if ("save".equals(action)) {
            return sendOk(citeCreatorService.editCiteText(id, text));
        }
        else throw new DioRuntimeException();
    }


}
