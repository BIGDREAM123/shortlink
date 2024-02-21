package org.lxq.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.lxq.shortlink.admin.common.biz.user.UserContext;
import org.lxq.shortlink.admin.common.convention.exception.ServiceException;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.database.BaseDo;
import org.lxq.shortlink.admin.dao.entity.GroupDo;
import org.lxq.shortlink.admin.dao.mapper.GroupMapper;
import org.lxq.shortlink.admin.remote.ShortLinkRemoteService;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.admin.service.RecycleBinService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 回收站实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    private ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDo> eq = Wrappers.lambdaQuery(GroupDo.class).eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(BaseDo::getDelFlag, 0);
        List<GroupDo> groupDos = groupMapper.selectList(eq);
        if (CollUtil.isEmpty(groupDos)){
            throw new ServiceException("用户名无分组信息");
        }
        requestParam.setGidList(groupDos.stream().map(GroupDo::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);

    }
}
