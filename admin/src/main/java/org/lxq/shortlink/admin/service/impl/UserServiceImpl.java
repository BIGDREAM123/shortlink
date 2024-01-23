package org.lxq.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dao.mapper.UserMapper;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> eq = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(eq);
        UserRespDTO res = new UserRespDTO();
        BeanUtils.copyProperties(userDo,res);
        return res;
    }
}
