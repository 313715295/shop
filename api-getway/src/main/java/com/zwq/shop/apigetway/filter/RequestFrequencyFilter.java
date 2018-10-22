package com.zwq.shop.apigetway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.zwq.shop.apigetway.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.RequestContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.zwq.shop.apigetway.enums.RedisKeyEnum.REQUEST_RECORD_KEY;
import static com.zwq.shop.apigetway.utils.Constant.*;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;

/**
 * created by zwq on 2018/10/22
 * 请求接口频率限制，过滤掉超出频率的请求，单ip，单接口，20秒接受50次请求
 */
@Slf4j
@Component
public class RequestFrequencyFilter extends ZuulFilter {

    private final String FIRST_TIME = "firstTime";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return SERVLET_DETECTION_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        return null;
    }

    /**
     * 过滤掉超频请求,使用hash存储，失效时间1礼拜，超过限定时间后重置值
     * 和使用string存储对比，不会频繁新建值和过期，但是可能需要开启事务，会不会影响性能？
     * 如果不必太精确，可以考虑不开启事务
     */
    private boolean check(HttpServletRequest request) {

        String ip = IpUtils.getRealIp(request);
        if (StringUtils.isEmpty(ip)) {
            log.warn("访问者ip为空~~");
            return false;
        }
        String url = request.getRequestURI();
        String key = REQUEST_RECORD_KEY.getKey() + ip;


        Long number = redisTemplate.opsForHash().increment(key, url, 1L);
        if (number == 1) {
            //第一次记录
            redisTemplate.opsForHash().put(key, FIRST_TIME, Instant.now().toEpochMilli());
        }
        long now = Instant.now().toEpochMilli();
        long firstTime = (long) redisTemplate.opsForHash().get(key, FIRST_TIME);


        if (now - firstTime > REQUEST_LIMIT_TIME) {
            //超过间隔时间，次数重置
            redisTemplate.opsForHash().delete(key, url);
        } else {
            if (number > REQUEST_NUMBER_LIMIT) {
                log.warn("【{}】请求接口【{}】太过频繁", ip, url);
                return false;
            }
        }

        Long liveTime = redisTemplate.getExpire(key, TimeUnit.HOURS);
        if (liveTime != null && liveTime == -1L) {
            //设定ip记录时间7天
            redisTemplate.expire(key, REQUEST_RECORD_LIVE_TIME, TimeUnit.HOURS);
        }
        return true;
    }

    /**
     * 设定一分钟的缓存，每次请求加1，超出频率拦截，1分钟后自动删除
     * 和上面方法一样，需要设定一个超时时间，考虑是否加事务
     */
    private boolean checkRequest(HttpServletRequest request) {

        String ip = IpUtils.getRealIp(request);
        if (StringUtils.isEmpty(ip)) {
            log.warn("访问者ip为空~~");
            return false;
        }
        String url = request.getRequestURI();
        String key = REQUEST_RECORD_KEY.getKey() + ip;


        Long number = redisTemplate.opsForValue().increment(key, 1L);
        if (number == null) {
            log.warn("{}访问redis计数异常", ip);
            return false;
        }

        if (number > REQUEST_NUMBER_LIMIT) {
            log.warn("【{}】请求接口【{}】太过频繁", ip, url);
            return false;
        }

        Long liveTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        if (liveTime != null && liveTime == -1L) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }
        return true;
    }


}
