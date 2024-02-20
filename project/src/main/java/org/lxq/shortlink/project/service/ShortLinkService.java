package org.lxq.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.lxq.shortlink.project.dao.entity.ShortLinkDO;
import org.lxq.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam
     * @return
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询短链接分组内数量
     * @param requestParam
     * @return
     */

    List<ShortLinkCountQueryRespDTO> listShortLinkCount(List<String> requestParam);

    /**
     * 更新短链接
     * @param requestParam
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转
     * @param shortUri
     * @param request
     * @param response
     */
    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response);
}
