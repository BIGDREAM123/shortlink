package org.lxq.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    /*@Autowired
    private UserService userService;*/
    private  final UserService userService;


    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result findByUsername(@PathVariable String username){
        UserRespDTO result = userService.getUserByUsername(username);
        if(result == null){
            return new Result<UserRespDTO>().setCode(UserErrorCodeEnum.USER_NULL.code()).
                    setMessage(UserErrorCodeEnum.USER_NULL.message());
        }else{
            return new Result<UserRespDTO>().setCode("0").setData(result);
        }


    }
}
