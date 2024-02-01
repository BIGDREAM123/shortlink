package org.lxq.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dto.req.UserLoginReqDTO;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserUpdateReqDTO;

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

    /**
     * 注册新用户
     * @param userRegisterReqDTO  用户信息
     */
    void register(UserRegisterReqDTO userRegisterReqDTO);

    /**
     * 修改用户信息
     * @param userUpdateReqDTO 要修改的用户信息
     */
    void update(UserUpdateReqDTO userUpdateReqDTO);

    /**
     *登录
     * @param userLoginReqDTO 登录信息
     * @return 用户登录返回信息 token
     */
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

    /**
     * 检查用户是否已登录
     * @param username 用户名
     * @param token  token
     * @return
     */
    Boolean checkLogin(String username,String token);

    /**
     * 注销登陆
     * @param username 用户名
     * @param token token
     */
    void logout(String username, String token);





}
