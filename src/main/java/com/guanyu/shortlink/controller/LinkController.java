package com.guanyu.shortlink.controller;


import com.guanyu.shortlink.common.Result;
import com.guanyu.shortlink.service.ILinkService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 短链接表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class LinkController {

    private final ILinkService linkService;

    @PostMapping
    public Result<String> create(@RequestBody CreateLinkRequest req) {
        String code = linkService.create(req.getOriginUrl());
        return Result.ok("http://localhost:8080/" + code);
    }

    @Data
    public static class CreateLinkRequest {
        private String originUrl;
    }


}
