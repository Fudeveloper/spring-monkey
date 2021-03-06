package xyz.tmlh.security.core.social.qq.config;

import static xyz.tmlh.security.core.support.PropertiesParam.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;

import xyz.tmlh.security.core.properties.TmlhSecurityProperties;
import xyz.tmlh.security.core.properties.social.QQProperties;
import xyz.tmlh.security.core.social.SocialAutoConfiguration;
import xyz.tmlh.security.core.social.qq.api.QQ;
import xyz.tmlh.security.core.social.qq.connet.QQConnectionFactory;
/*
 * 需要预先建立表 create table UserConnection (userId varchar(255) not null, providerId varchar(255) not null, providerUserId
 * varchar(255), rank int not null, displayName varchar(255), profileUrl varchar(512), imageUrl varchar(512),
 * accessToken varchar(512) not null, secret varchar(512), refreshToken varchar(512), expireTime bigint, primary key
 * (userId, providerId, providerUserId)); create unique index UserConnectionRank on UserConnection(userId, providerId,
 * rank);
 * 
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
@ConditionalOnProperty(prefix = PREFIX_SOCIAL_QQ, name = PARAM_APP_ID)
public class QQAutoConfiguration  extends SocialAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(QQAutoConfiguration.class);
    
    @Autowired
    protected TmlhSecurityProperties tmlhSecurityProperties;
    
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public QQ gitQQ(ConnectionRepository repository) {
        Connection<QQ> connection = repository.findPrimaryConnection(QQ.class);
        return connection != null ? connection.getApi() : null;
    }
    
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
         super.addConnectionFactories(connectionFactoryConfigurer, environment);
         QQProperties qq = tmlhSecurityProperties.getSocial().getQq();
         QQConnectionFactory qqConnectionFactory = new QQConnectionFactory(qq.getProviderId(), qq.getAppId(), qq.getAppSecret());
         connectionFactoryConfigurer.addConnectionFactory(qqConnectionFactory);
         LOGGER.info("社交登陆qq配置成功!"); 
    }
}
