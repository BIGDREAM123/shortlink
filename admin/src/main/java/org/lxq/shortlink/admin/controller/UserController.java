package org.lxq.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.dto.req.UserLoginReqDTO;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserActualRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserUpdateReqDTO;
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
    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO userUpdateReqDTO){
        userService.update(userUpdateReqDTO);
        return Results.success();
    }

    /**
     * 用户登录
     * @param userLoginReqDTO 登录信息
     * @return  用户登录返回信息 token
     */
    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO){
        UserLoginRespDTO userLoginRespDTO= userService.login(userLoginReqDTO);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 检查用户是否登录
     * @param username 用户名
     * @param token token
     * @return  true代表已登录
     */
    @GetMapping("/api/short-link/v1/user/check-login")
    public  Result<Boolean> checkLogin(@RequestParam("username") String username,
                                       @RequestParam("token") String token){
        return Results.success(userService.checkLogin(username,token));
    }

    @DeleteMapping("/api/short-link/v1/user/logout")
    public  Result<Void> logout(@RequestParam("username") String username,
                                @RequestParam("token") String token){
            userService.logout(username,token);
            return Results.success();
    }
}
