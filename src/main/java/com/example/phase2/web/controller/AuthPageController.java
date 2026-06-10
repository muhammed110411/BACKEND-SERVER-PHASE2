package com.example.phase2.web.controller;

import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.viewmodel.LoginPageViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/login")
public class AuthPageController {

    private static final String LOGIN_VIEW_NAME = "admin/login";
    private static final String LOGIN_MODEL_ATTRIBUTE = "login";
    private static final String LOGIN_PROCESSING_PATH = "/admin/login";
    private static final String GENERIC_LOGIN_ERROR_MESSAGE = "Invalid username or password.";

    private final PageViewModelMapper pageViewModelMapper;

    public AuthPageController(PageViewModelMapper pageViewModelMapper) {
        this.pageViewModelMapper = pageViewModelMapper;
    }

    @GetMapping(params = {"!error", "!logout"})
    public String getLoginPage(Model model) {
        return renderLoginPage(false, false, model);
    }

    @GetMapping
    public String getLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model
    ) {
        return renderLoginPage(hasSignal(error), hasSignal(logout), model);
    }

    private String renderLoginPage(boolean hasError, boolean hasLogout, Model model) {
        LoginPageViewModel viewModel = pageViewModelMapper.toLoginPageViewModel(
                null,
                hasError ? GENERIC_LOGIN_ERROR_MESSAGE : null,
                false,
                null,
                null
        );

        model.addAttribute(LOGIN_MODEL_ATTRIBUTE, viewModel);
        model.addAttribute("logoutSuccess", hasLogout);
        model.addAttribute("loginProcessingPath", LOGIN_PROCESSING_PATH);
        return LOGIN_VIEW_NAME;
    }

    private static boolean hasSignal(String value) {
        return value != null;
    }
}
