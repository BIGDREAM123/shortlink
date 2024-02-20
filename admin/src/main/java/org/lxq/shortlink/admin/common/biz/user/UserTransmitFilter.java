package org.lxq.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.lxq.shortlink.admin.common.convention.exception.ClientException;
import org.lxq.shortlink.admin.common.convention.result.Results;
import org.lxq.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 用户信息传输过滤器
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {
    private  final StringRedisTemplate stringRedisTemplate;
    private static final List<String> IGNORE_URL = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username");

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if(!IGNORE_URL.contains(requestURI)){
            if(!("/api/short-link/admin/v1/user".equals(requestURI)&&("POST".equals(((HttpServletRequest) servletRequest).getMethod())))){
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            if(!StrUtil.isAllNotBlank(username,token)){
                    returnJson((HttpServletResponse) servletResponse,JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                    return;
            }
                Object userInfoJsonStr;
            try {
                userInfoJsonStr  = stringRedisTemplate.opsForHash().get("login_" + username, token);
                if(userInfoJsonStr ==null){
                    throw new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL);
                }

            }catch (Exception ex){
                returnJson((HttpServletResponse) servletResponse,JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                return;
            }
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }

    }

    /**
     * 返回客户端数据
     * @param response
     * @param json
     * @throws Exception
     */

    private void returnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
        } finally {
            if (writer != null)
                writer.close();
        }
    }

}