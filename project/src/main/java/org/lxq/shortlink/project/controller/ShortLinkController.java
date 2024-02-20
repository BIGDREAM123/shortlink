package org.lxq.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.project.common.convention.result.Result;
import org.lxq.shortlink.project.common.convention.result.Results;
import org.lxq.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private  final ShortLinkService shortLinkService;


    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request,
                           ServletResponse response){
        shortLinkService.restoreUrl(shortUri,request,response);

    }

    /**
     * 新建短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.createShortLink(requestParam))   ;
    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping ("/api/short-link/v1/group")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));

    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping ("/api/short-link/v1/count")
    public Result<List<ShortLinkCountQueryRespDTO>> listShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
        return Results.success(shortLinkService.listShortLinkCount(requestParam));
    }

    /**
     * 修改短链接
     * @param requestParam
     * @return 空
     */

    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }





}
