package org.lxq.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDo> {
    UserRespDTO getUserByUsername(String username);
}
