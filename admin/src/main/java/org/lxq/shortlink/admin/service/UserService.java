package org.lxq.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService extends IService<UserDo> {
    /**
     * 根据用户名查找用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否已经存在
     * @param username 用户名
     * @return 用户名存在返回false 不存在返回true
     */
    Boolean hasUserName(String username);

    void register(@RequestBody UserRegisterReqDTO userRegisterReqDTO);


}
