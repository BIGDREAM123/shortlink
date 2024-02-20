package org.lxq.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxq.shortlink.admin.dao.entity.GroupDo;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.lxq.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.lxq.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDo> {
    /**
     *  新增短链接分组
      * @param groupName 短链接分组名
     */
    void save(String groupName);

    /**
     * 新增短链接分组
     * @param username  用户名
     * @param groupName  短链接分组名
     */
    void save(String username,String groupName);

    List<ShortLinkGroupRespDTO> listGroup();

    void updateGroup(ShortLinkGroupUpdateReqDTO requsetParam);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
