package com.guanyu.shortlink.controller;

import com.guanyu.shortlink.service.ILinkService;
import com.guanyu.shortlink.stat.AccessStatProducer;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AccessStatProducer statProducer;

    @GetMapping("/{code}")
    public void redirect(@PathVariable String code,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String originUrl = linkService.getOriginUrl(code);

        statProducer.send(code, getClientIp(request));

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", originUrl);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            return request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}