package org.lxq.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserActualRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserController {
    /*@Autowired
    private UserService userService;*/
    private  final UserService userService;


    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username){
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);

    }

    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username){
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(BeanUtil.toBean(result,UserActualRespDTO.class));

    }

    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username){
        return Results.success(userService.hasUserName(username));
    }

    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO userRegisterReqDTO){
        userService.register(userRegisterReqDTO);
        return Results.success();
    }
}
