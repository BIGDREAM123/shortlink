package org.lxq.shortlink.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 有效期类型
 */

@AllArgsConstructor
public enum ValiDateTypeEnum {
    /**
     * 永久有效
     */
    PERMANENT(0),
    /**
     * 自定义有效期
     */
    CUSTOM(1);


    @Getter
    private final int type;
}
