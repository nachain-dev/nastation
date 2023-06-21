package org.nastation.web.controller;

import org.nastation.module.pub.data.PageState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"/404"})
    public String _404(Model model) {
        return "redirect:/PageStateView?type="+ PageState.PAGE_NOT_FOUND.value;
    }

    @GetMapping({"/500"})
    public String _500(Model model) {
        return "redirect:/PageStateView?type="+ PageState.SYSTEM_ERROR.value;
    }


}