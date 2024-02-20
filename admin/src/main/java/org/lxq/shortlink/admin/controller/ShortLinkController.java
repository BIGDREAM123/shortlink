package org.lxq.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.remote.dto.ShortLinkRemoteService;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkController {
    private ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 新建短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public  Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return shortLinkRemoteService.createShortLink(requestParam);

    }

    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

}
