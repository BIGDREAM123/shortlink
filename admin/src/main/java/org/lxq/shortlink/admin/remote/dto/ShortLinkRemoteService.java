package org.lxq.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.lxq.shortlink.admin.common.convention.result.Result;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.lxq.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.lxq.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {
    /**
     * 远程调用创建短链接
     * @param requestParam
     * @return
     */
    default  public Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create",
                JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
        });


    }

    /**
     * 远程调用分页查询
     * @param requestParam
     * @return
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/group",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>(){});
    }

    /**
     * 远程调用查询短链接数量
     * @param requestParam
     * @return
     */
    default public Result<List<ShortLinkCountQueryRespDTO>> listShortLinkCount(List<String> requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("requestParam",requestParam);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>(){});
    }

    /**
     * 远程调用修改短链接
     * @param requestParam
     * @return
     */

    default public void updateShortLink(ShortLinkUpdateReqDTO requestParam){
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update",JSON.toJSONString(requestParam));
        return;

    }
}
