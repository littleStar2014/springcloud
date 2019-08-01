package com.andychan.gateway.filter;

import com.andychan.gateway.utils.ObjectJsonUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;

@Configuration
public class AuthFilter extends ZuulFilter {
    private final static String AUTHORIZATION = "Authorization";
    private final static String ACCESS_TOKEN = "X-Access-Token";
    private final static Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate ;
    @Resource
    RedisConnectionFactory redisConnectionFactory;
    //拦截类型
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    //拦截顺序
    @Override
    public int filterOrder() {
        return SERVLET_DETECTION_FILTER_ORDER - 1;
    }

    //开启拦截
    @Override
    public boolean shouldFilter() {
        return true;
    }

    //拦截处理逻辑
    @Override
    public Object run() {
        //获取用户认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info(authentication.toString());
        RequestContext requestContext = RequestContext.getCurrentContext();
        if(!requestContext.getRequest().getRequestURL().toString().contains("/oauth/")) {
            String token = requestContext.getRequest().getHeader(AUTHORIZATION);
            boolean tokenCheckPassed = false;
            if (token != null && !token.isEmpty()) {
                String redisTokenKey = "access:" + token;
                tokenCheckPassed = redisTemplate.hasKey(redisTokenKey);
                logger.info("Access token: {}, redis token key {} exist: {}", token, redisTokenKey,tokenCheckPassed);
                RedisConnection conn = redisConnectionFactory.getConnection();
                try {
                    RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
                    byte[] key  = serializationStrategy.serialize(redisTokenKey);
                    byte[] bytes = conn.get(key);
                    OAuth2AccessToken content = serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
                    logger.info("Redis key: {}, value: {}", redisTokenKey, ObjectJsonUtils.getJsonStringFromObject(content));
                } catch (Exception e){
                    e.printStackTrace();
                } finally{
                    conn.close();
                }
            }
            if(token == null || token.isEmpty() || !tokenCheckPassed) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(401);
                try {
                    requestContext.getResponse().setHeader("Content-Type", "text/html;charset=UTF-8");
                    requestContext.getResponse().getWriter().write("token无效");
                } catch (Exception e) {

                }
            }
        }
        /*
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if (request.getUserPrincipal() != null && request.getUserPrincipal() instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) request.getUserPrincipal();
            if (authentication.getDetails() != null &&
                    authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                logger.info(details.getTokenValue());
            }
        }*/


        return null;
    }
}
