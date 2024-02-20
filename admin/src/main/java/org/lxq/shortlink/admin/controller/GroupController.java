package org.lxq.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.lxq.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import org.lxq.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     * @param requsetParam  短链接分组名称
     * @return 无
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requsetParam){
        groupService.save(requsetParam.getName());
        return Results.success();
    }
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){

        return  Results.success(groupService.listGroup());
    }

    /**
     * 更新短链接分组
     * @param requsetParam 短链接分组名称
     * @return 无
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> update(@RequestBody ShortLinkGroupUpdateReqDTO requsetParam){
        groupService.updateGroup(requsetParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     * @param gid 短链接分组id
     * @return 无
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 排序
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam){
        groupService.sortGroup(requestParam);
        return Results.success();

    }







}
