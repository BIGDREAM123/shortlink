package org.lxq.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.project.common.convention.result.Result;
import org.lxq.shortlink.project.common.convention.result.Results;
import org.lxq.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import org.lxq.shortlink.project.dto.req.*;
import org.lxq.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.project.service.RecycleBinService;
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
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return  Results.success();
    }


    /**
     * 回收站分页查询
     * @param requestParam gid
     * @return
     */
    @GetMapping ("/api/short-link/v1/recycle-bin/group")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }

    /**
     * 从回收站中恢复短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }


    /**
     * 从回收站中移除短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam){
        recycleBinService.removeRecycleBin(requestParam);
        return Results.success();

    }
}
