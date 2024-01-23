package org.lxq.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
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
    public UserRespDTO findByUsername(@PathVariable String username){
        return userService.getUserByUsername(username);


    }
}
