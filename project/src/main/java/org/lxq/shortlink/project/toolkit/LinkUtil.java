package org.lxq.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.lxq.shortlink.project.common.constant.ShortLinkConstant;

import java.util.Date;
import java.util.Optional;

public class LinkUtil {
    /**
     * 获取短链接缓存有效期时间
     * @param validDate 有效期
     * @return 有效期时间戳
     */
    public static long getLinkCacheValidDateTime(Date validDate){
        return Optional.ofNullable(validDate).map(each-> DateUtil.between(new Date(),each,DateUnit.MS))
                .orElse(ShortLinkConstant.DEFAULT_CACHE_VALID_TIME);
    }
}
