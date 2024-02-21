package org.lxq.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.project.service.UrlTitleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.lxq.shortlink.project.common.convention.result.*;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final UrlTitleService urlTitleService;
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url){
        return  Results.success(urlTitleService.getTitleByUrl(url));

    }
}
