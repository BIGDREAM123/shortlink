package org.lxq.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lxq.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDTO {
    private Long id;
    private String username;

    private String realName;
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    private String mail;
    private Long deletionTime;
    private Date createTime;
    private Date updateTime;
    private Integer delFlag;
}
