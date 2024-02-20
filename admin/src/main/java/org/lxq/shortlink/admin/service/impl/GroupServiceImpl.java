package org.lxq.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import groovy.util.logging.Slf4j;
import org.lxq.shortlink.admin.common.biz.user.UserContext;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.common.database.BaseDo;
import org.lxq.shortlink.admin.dao.entity.GroupDo;
import org.lxq.shortlink.admin.dao.mapper.GroupMapper;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.lxq.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import org.lxq.shortlink.admin.remote.dto.ShortLinkRemoteService;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.lxq.shortlink.admin.service.GroupService;
import org.lxq.shortlink.admin.toolkit.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {
    private ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };
    @Override
    public void save(String groupName) {
        save(UserContext.getUsername(),groupName);

    }

    @Override
    public void save(String username, String groupName) {
        String gid;
        do{
            gid = RandomGenerator.generateRandomString();

        }while(!hasGid(username,gid));
        GroupDo groupDo = GroupDo.builder()
                .gid(gid)
                .sortOrder(0)
                .username(username)
                .name(groupName)
                .build();
        baseMapper.insert(groupDo);

    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDo> groupDoLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDo.class)
               .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(BaseDo::getDelFlag,0).
                orderByDesc(GroupDo::getSortOrder).orderByDesc(BaseDo::getUpdateTime);
        List<GroupDo> groupDoList = baseMapper.selectList(groupDoLambdaQueryWrapper);
        Result<List<ShortLinkCountQueryRespDTO>> listResult =
                shortLinkRemoteService.listShortLinkCount(groupDoList.stream().map(GroupDo::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList =
                BeanUtil.copyToList(groupDoList, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOList.forEach(each->{
            Optional<ShortLinkCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item-> Objects.equals(item.getGid(),each.getGid()))
                    .findFirst();
            first.ifPresent(item->each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOList;

    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requsetParam) {
        LambdaUpdateWrapper<GroupDo> eq = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid, requsetParam.getGid())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = GroupDo.builder().name(requsetParam.getName()).build();
        baseMapper.update(groupDo,eq);

    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDo> eq = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid, gid)
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setDelFlag(1);
        baseMapper.update(groupDo,eq);


    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each->{
            LambdaUpdateWrapper<GroupDo> eq = Wrappers.lambdaUpdate(GroupDo.class)
                    .eq(GroupDo::getUsername, UserContext.getUsername())
                    .eq(GroupDo::getGid, each.getGid())
                    .eq(GroupDo::getDelFlag, 0);
            GroupDo groupDo = new GroupDo();
            groupDo.setSortOrder(each.getSortOrder());
            baseMapper.update(groupDo,eq);
    });
    }

    private boolean hasGid(String username,String gid){
        LambdaQueryWrapper<GroupDo> eq = Wrappers.lambdaQuery(GroupDo.class).eq(GroupDo::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDo::getUsername,Optional.ofNullable(username).orElse(UserContext.getUsername()));
        return baseMapper.selectOne(eq) == null;
    }


}
