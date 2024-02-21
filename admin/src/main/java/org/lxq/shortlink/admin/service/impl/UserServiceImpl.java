package org.lxq.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.constant.RedisCacheConstant;
import org.lxq.shortlink.admin.common.convention.exception.ClientException;
import org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.lxq.shortlink.admin.dao.entity.UserDo;
import org.lxq.shortlink.admin.dao.mapper.UserMapper;
import org.lxq.shortlink.admin.dto.req.UserLoginReqDTO;
import org.lxq.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.lxq.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.lxq.shortlink.admin.dto.resp.UserRespDTO;
import org.lxq.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.lxq.shortlink.admin.service.GroupService;
import org.lxq.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;

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
                try {
                    int insert = baseMapper.insert(BeanUtil.toBean(userRegisterReqDTO, UserDo.class));
                    if(insert <1){
                        throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                    }
                }catch (DuplicateKeyException ex){
                    throw  new ClientException(USER_EXIST);
                }

                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
                groupService.save(userRegisterReqDTO.getUsername(),"默认分组");
                return;
            }else{
                throw new ClientException(USER_NAME_NULL);
            }

        }finally {
            lock.unlock();
        }




    }

    @Override
    public void update(UserUpdateReqDTO userUpdateReqDTO) {
        //TODO 验证当前用户是否登录
        LambdaUpdateWrapper<UserDo> eq = Wrappers.lambdaUpdate(UserDo.class).eq(UserDo::getUsername, userUpdateReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(userUpdateReqDTO,UserDo.class),eq);

    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        LambdaQueryWrapper<UserDo> eq = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDo::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDo::getDelFlag, 0);
        UserDo userDo = baseMapper.selectOne(eq);
        if(userDo == null){
            throw  new ClientException("用户不存在");
        }
        Boolean hasLogin = stringRedisTemplate.hasKey("login_"+userLoginReqDTO.getUsername());
        if(hasLogin != null && hasLogin){
            throw  new ClientException("用户已登录");
        }

        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+userLoginReqDTO.getUsername(),
                uuid, JSON.toJSONString(userDo));
        stringRedisTemplate.expire("login_"+userLoginReqDTO.getUsername(),30L, TimeUnit.DAYS);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_"+username,token) !=null;
    }

    @Override
    public void logout(String username, String token) {
        Boolean haslogin = checkLogin(username, token);
        if(haslogin != null && haslogin){
            stringRedisTemplate.delete("login_"+username);
            return;
        }
        throw  new ClientException("用户名未登录或用户Token不存在");


    }
}
