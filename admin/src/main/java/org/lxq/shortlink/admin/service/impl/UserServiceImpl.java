package org.lxq.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.constant.RedisCacheConstant;
import org.lxq.shortlink.admin.common.convention.exception.ClientException;
import org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dao.mapper.UserMapper;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_NULL;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> eq = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(eq);
        if(userDo == null){
            throw new ClientException(UserErrorCodeEnum.USER_NULL);

        }
        UserRespDTO res = new UserRespDTO();
        BeanUtils.copyProperties(userDo,res);
        return res;
    }

    @Override
    public Boolean hasUserName(String username) {

        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO userRegisterReqDTO) {
        if(!hasUserName(userRegisterReqDTO.getUsername())){
            throw  new ClientException(USER_NAME_NULL);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + userRegisterReqDTO.getUsername());
        try {
            if(lock.tryLock()){
                int insert = baseMapper.insert(BeanUtil.toBean(userRegisterReqDTO, UserDo.class));
                if(insert <1){
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
            }else{
                throw new ClientException(USER_NAME_NULL);
            }

        }finally {
            lock.unlock();

        }




    }
}
