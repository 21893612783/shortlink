package com.guanyu.shortlink.controller;

import com.guanyu.shortlink.service.ILinkService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final ILinkService linkService;

    @GetMapping("/{code}")
    public void redirect(@PathVariable String code,
                         HttpServletResponse response) throws IOException {
        String originUrl = linkService.getOriginUrl(code);

        response.setStatus(HttpServletResponse.SC_FOUND);   // 302
        response.setHeader("Location", originUrl);
    }
}