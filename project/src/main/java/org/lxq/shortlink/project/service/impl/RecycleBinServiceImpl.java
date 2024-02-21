package org.lxq.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.project.common.constant.RedisKeyConstant;
import org.lxq.shortlink.project.dao.entity.ShortLinkDO;
import org.lxq.shortlink.project.dao.mapper.ShortLinkMapper;
import org.lxq.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.lxq.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import org.lxq.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.project.service.RecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 回收站接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> eq = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        ShortLinkDO build = ShortLinkDO.builder()
                .enableStatus(1)
                .build();

        baseMapper.update(build,eq);
        stringRedisTemplate.delete(
                String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY,requestParam.getFullShortUrl())
        );

    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, requestParam.getGidList())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 1);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam,eq);
        return  resultPage.convert(each->{
                    ShortLinkPageRespDTO bean = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
                    bean.setDomain("http://"+bean.getDomain());
                    return bean;
                }
        );

    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> eq = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 1);

        ShortLinkDO build = ShortLinkDO.builder()
                .enableStatus(0)
                .build();

        baseMapper.update(build,eq);
        stringRedisTemplate.delete(String.format(
                RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY,requestParam.getFullShortUrl()));

    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        baseMapper.delete(eq);


    }
}
