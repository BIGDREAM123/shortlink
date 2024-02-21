package org.lxq.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.remote.ShortLinkRemoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };
    @GetMapping("/api/short-link/admin/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url){
        return  shortLinkRemoteService.getTitleByUrl(url);

    }
}
