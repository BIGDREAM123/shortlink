package org.lxq.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lxq.shortlink.project.common.constant.RedisKeyConstant;
import org.lxq.shortlink.project.common.convention.exception.ClientException;
import org.lxq.shortlink.project.common.convention.exception.ServiceException;
import org.lxq.shortlink.project.common.enums.ValiDateTypeEnum;
import org.lxq.shortlink.project.dao.entity.ShortLinkDO;
import org.lxq.shortlink.project.dao.entity.ShortLinkGotoDO;
import org.lxq.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import org.lxq.shortlink.project.dao.mapper.ShortLinkMapper;
import org.lxq.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.lxq.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.lxq.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.lxq.shortlink.project.service.ShortLinkService;
import org.lxq.shortlink.project.toolkit.HashUtil;
import org.lxq.shortlink.project.toolkit.LinkUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 短链接接口实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {

        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain()+"/"+shortLinkSuffix;
        ShortLinkDO shortLinkDo = ShortLinkDO.builder()
                .fullShortUrl(fullShortUrl)
                .domain(requestParam.getDomain())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .originUrl(requestParam.getOriginUrl())
                .favicon(getFavicon(requestParam.getOriginUrl()))
                .build();
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder().gid(requestParam.getGid()).fullShortUrl(fullShortUrl).build();
        try {
            baseMapper.insert(shortLinkDo);
            shortLinkGotoMapper.insert(linkGotoDO);
        }catch (DuplicateKeyException ex){
            LambdaQueryWrapper<ShortLinkDO> eq =
                    Wrappers.lambdaQuery(ShortLinkDO.class).eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(eq);
            if (hasShortLinkDO != null){
                log.warn("短链接：{} 重复入库",fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }
        }
        stringRedisTemplate.opsForValue().set(
                String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY,fullShortUrl)
        ,requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDateTime(requestParam.getValidDate())
                ,TimeUnit.MILLISECONDS);
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);

        ShortLinkCreateRespDTO build = ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://"+shortLinkDo.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
        return build;



    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam,eq);
       return  resultPage.convert(each->{
                   ShortLinkPageRespDTO bean = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
                   bean.setDomain("http://"+bean.getDomain());
                   return bean;
               }
            );

    }

    /**
     * 查询短链接数量
     * @param requestParam gid的列表
     * @return
     */
    @Override
    public List<ShortLinkCountQueryRespDTO> listShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> shortLinkDOQueryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid,count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(shortLinkDOQueryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkCountQueryRespDTO.class);
    }

    /**
     * 修改短链接
     * @param requestParam 短链接修改请求参数
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(eq);
        if(hasShortLinkDO == null){
            throw new ClientException("短链接记录不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate()).build();
        if (Objects.equals(hasShortLinkDO.getGid(),requestParam.getGid())){
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValiDateTypeEnum.PERMANENT.getType()),
                            ShortLinkDO::getValidDate, null);
            baseMapper.update(shortLinkDO,updateWrapper);
        }else{
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);

        }



    }

    /**
     * 短链接跳转
     * @param shortUri 短链接后缀
     * @param request 请求
     * @param response 响应
     * */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName+"/"+shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StringUtil.isNotBlank(originalLink)){
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if(!contains){
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(
                RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl));
        if(StringUtil.isNotBlank(gotoIsNullShortLink)){
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }

        RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
            if(StringUtil.isNotBlank(originalLink)){
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (linkGotoDO == null){
                // 需要封控
                stringRedisTemplate.opsForValue().set(String.format(
                        RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),"-",30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, linkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(eq);
            if(shortLinkDO == null || (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date()))){
                    stringRedisTemplate.opsForValue().set(
                            String.format(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),
                            "-",30, TimeUnit.MINUTES);
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    return;
                }
                stringRedisTemplate.opsForValue().set(
                        String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY,fullShortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidDateTime(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS);
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());

        }finally {
            lock.unlock();
        }

    }

    /**
     * 生成短链接 URI
     * @param requestParam
     * @return
     */
    private String generateSuffix(ShortLinkCreateReqDTO requestParam){
        String originUrl = requestParam.getOriginUrl();
        int customGenerateCount =0;
        String shortUri ;
        while (true){
            if (customGenerateCount>10){
                throw  new ServiceException("短链接频繁生成，请稍后再试");
            }
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl()+System.currentTimeMillis());
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain()+"/"+shortUri)) {
                break;

            }
            customGenerateCount++;

        }
        return shortUri;
    }

    /**
     * 获取网站的favicon图标链接
     * @param url 网站的url
     * @return  favicon的图标链接 不存在返回null
     */
    @SneakyThrows
    private String getFavicon(String url){
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)targetUrl.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP){
            String redirectUrl = connection.getHeaderField("Location");
            if (redirectUrl !=null){
                URL newUrl = new URL(redirectUrl);
                connection = (HttpURLConnection) newUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                responseCode = connection.getResponseCode();
            }
        }
        if (responseCode == HttpURLConnection.HTTP_OK){
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink !=null){
                return faviconLink.attr("abs:href");
            }
        }
        return null;



    }
}
