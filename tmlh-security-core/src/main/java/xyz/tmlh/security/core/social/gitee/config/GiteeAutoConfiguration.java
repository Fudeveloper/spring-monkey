package xyz.tmlh.security.core.social.gitee.config;

import static xyz.tmlh.security.core.support.PropertiesParam.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;

import xyz.tmlh.security.core.properties.TmlhSecurityProperties;
import xyz.tmlh.security.core.properties.social.GiteeProperties;
import xyz.tmlh.security.core.social.SocialAutoConfiguration;
import xyz.tmlh.security.core.social.gitee.connet.GiteeConnectionFactory;

/*
  需要预先建立表 create table UserConnection (userId varchar(255) not null, providerId varchar(255) not null, providerUserId
  varchar(255), rank int not null, displayName varchar(255), profileUrl varchar(512), imageUrl varchar(512),
  accessToken varchar(512) not null, secret varchar(512), refreshToken varchar(512), expireTime bigint, primary key
  (userId, providerId, providerUserId)); create unique index UserConnectionRank on UserConnection(userId, providerId,
 rank);
 */
/**
 * <p>
 * QQ登陆的配置类</br>
 * 当配置了tmlh.security.social.qq.app-id才希望生效
 * </p>
 *
 * @author TianXin
 * @since 2019年3月22日上午11:41:29
 */
@EnableSocial
@ConditionalOnClass(SocialAutoConfiguration.class)
@Configuration
@ConditionalOnProperty(prefix = PREFIX_SOCIAL_GITEE, name = PARAM_APP_ID)
public class GiteeAutoConfiguration extends SocialAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiteeAutoConfiguration.class);
    
    @Autowired
    protected TmlhSecurityProperties tmlhSecurityProperties;
    
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
         super.addConnectionFactories(connectionFactoryConfigurer, environment);
         GiteeProperties gitee = tmlhSecurityProperties.getSocial().getGitee();
         GiteeConnectionFactory giteeConnectionFactory = new GiteeConnectionFactory(gitee.getProviderId(), gitee.getAppId(), gitee.getAppSecret());
         connectionFactoryConfigurer.addConnectionFactory(giteeConnectionFactory);
         LOGGER.info("社交登陆gitee配置成功!"); 
    }
    
}
