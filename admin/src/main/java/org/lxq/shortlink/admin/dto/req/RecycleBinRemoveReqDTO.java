package org.lxq.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回收站移除参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecycleBinRemoveReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
