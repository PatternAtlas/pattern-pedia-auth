package com.patternpedia.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;

@Slf4j
@Configuration
//@EnableConfigurationProperties(SecurityProperties.class)
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(AuthorizationServerConfig.class);

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userService;
//    private final SecurityProperties securityProperties;

//    @Autowired
//    private final DataSource dataSource;

    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private TokenStore tokenStore;

    @Value("${jwt.clientId:pattern-pedia-client}")
    private String clientId;

    @Value("${jwt.client-secret:iamaghost}")
    private String clientSecret;

    @Value("${jwt.accessTokenValidititySeconds:43200}") // 12 hours
    private int accessTokenValiditySeconds;

    @Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
    private String[] authorizedGrantTypes;

    @Value("${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
    private int refreshTokenValiditySeconds;

    public AuthorizationServerConfig(final AuthenticationManager authenticationManager, final PasswordEncoder passwordEncoder,
                                     final UserDetailsService userService
//                                     final SecurityProperties securityProperties
//                                     final DataSource dataSource
    ) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
//        this.securityProperties = securityProperties;
//        this.dataSource = dataSource;
    }

//    @Override
//    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(this.dataSource);
//    }
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        log.info(clients.toString());
//        clients.jdbc(this.dataSource);
        clients.inMemory()
                .withClient(clientId)
                .secret(passwordEncoder.encode(clientSecret))
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .authorizedGrantTypes(authorizedGrantTypes)
                .scopes("read", "write")
                .resourceIds("user/**");
//                .redirectUris("http://localhost:4200");
    }


    //    @Override
//    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
//        endpoints.authenticationManager(this.authenticationManager)
//                .accessTokenConverter(jwtAccessTokenConverter())
//                .userDetailsService(this.userDetailsService)
//                .tokenStore(tokenStore());
//    }
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .accessTokenConverter(jwtAccessTokenConverter())
                .userDetailsService(this.userService)
                .authenticationManager(this.authenticationManager);
//                .tokenStore(tokenStore());
    }

    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        return converter;
    }

//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        if (jwtAccessTokenConverter != null) {
//            return jwtAccessTokenConverter;
//        }
//
//        SecurityProperties.JwtProperties jwtProperties = securityProperties.getJwt();
//        KeyPair keyPair = this.keyPair(jwtProperties, keyStoreKeyFactory(jwtProperties));
//
//        jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setKeyPair(keyPair);
//        return jwtAccessTokenConverter;
//    }
//
//    @Bean
//    public TokenStore tokenStore() {
//        if (tokenStore == null) {
//            tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
//        }
//        return tokenStore;
//    }
//
//    @Bean
//    public DefaultTokenServices tokenServices(final TokenStore tokenStore,
//                                              final ClientDetailsService clientDetailsService) {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        tokenServices.setSupportRefreshToken(true);
//        tokenServices.setTokenStore(tokenStore);
//        tokenServices.setClientDetailsService(clientDetailsService);
//        tokenServices.setAuthenticationManager(this.authenticationManager);
//        return tokenServices;
//    }
//
//    @Override
//    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
//        oauthServer.passwordEncoder(this.passwordEncoder).tokenKeyAccess("permitAll()")
//                .checkTokenAccess("isAuthenticated()");
//    }
//
//    private KeyPair keyPair(SecurityProperties.JwtProperties jwtProperties, KeyStoreKeyFactory keyStoreKeyFactory) {
//        return keyStoreKeyFactory.getKeyPair(jwtProperties.getKeyPairAlias(), jwtProperties.getKeyPairPassword().toCharArray());
//    }
//
//    private KeyStoreKeyFactory keyStoreKeyFactory(SecurityProperties.JwtProperties jwtProperties) {
//        return new KeyStoreKeyFactory(jwtProperties.getKeyStore(), jwtProperties.getKeyStorePassword().toCharArray());
//    }

}
