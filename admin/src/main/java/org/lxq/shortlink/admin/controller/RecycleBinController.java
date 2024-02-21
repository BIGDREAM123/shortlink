package org.lxq.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import org.lxq.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import org.lxq.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import org.lxq.shortlink.admin.remote.ShortLinkRemoteService;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.admin.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;
    private ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 保存回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站分页查询
     * @param requestParam gid
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/group")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return recycleBinService.pageRecycleBinShortLink(requestParam);

    }

    /**
     * 恢复短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        shortLinkRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 从回收站中移除短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam){
        shortLinkRemoteService.removeRecycleBin(requestParam);
        return Results.success();

    }
}
