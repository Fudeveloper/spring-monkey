package xyz.tmlh.security.core.validate.code;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.tmlh.security.core.exception.ValidateCodeException;
import xyz.tmlh.security.core.properties.TmlhSecurityProperties;
import xyz.tmlh.security.core.support.SecurityConstants;
import xyz.tmlh.security.core.validate.code.support.ImageCode;
import xyz.tmlh.security.core.validate.code.support.ValidateCodeType;

/**
 * <p>
 *   验证码过滤器
 * </p>
 *
 * @author TianXin
 * @since 2019年7月10日下午1:39:38
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {
    
    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 系统配置信息
     */
    @Autowired
    private TmlhSecurityProperties tmlhSecurityProperties;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private Set<String> urls = new HashSet<>();

    /**
     * 组装urls
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        String url = tmlhSecurityProperties.getCode().getImage().getUrl();
        if (StringUtils.isNotBlank(url)) {
            String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(url, ",");
            for (String configUrl : configUrls) {
                urls.add(configUrl);
            }
        }
        // 登陆的请求一定要验证码
        urls.add(tmlhSecurityProperties.getBrowser().getLoginProcessingUrl());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean action = false;
        for (String url : urls) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                action = true;
            }
        }
        if (action) {
            try {
                validate(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * So here's the comment
     * 
     * @throws ServletRequestBindingException
     */
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode)sessionStrategy.getAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + ValidateCodeType.IMAGE.toString());
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE);
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在");
        }

        if (codeInSession.isExpried()) {
            sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + ValidateCodeType.IMAGE.getParamNameOnValidate());
            throw new ValidateCodeException("验证码已过期");

        }
        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + ValidateCodeType.IMAGE.getParamNameOnValidate());
            throw new ValidateCodeException("验证码不匹配");
        }

        sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + ValidateCodeType.IMAGE.getParamNameOnValidate());

    }

    public TmlhSecurityProperties getSecurityProperties() {
        return tmlhSecurityProperties;
    }

    public void setSecurityProperties(TmlhSecurityProperties securityProperties) {
        this.tmlhSecurityProperties = securityProperties;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public AntPathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(AntPathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public SessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

}
