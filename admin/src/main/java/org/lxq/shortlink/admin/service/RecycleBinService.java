package org.lxq.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * URL 回收站接口层
 */
public interface RecycleBinService {

    /**
     * 分页查询回收站链接
     */

    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
