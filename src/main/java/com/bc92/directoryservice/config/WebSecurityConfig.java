package com.bc92.directoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import com.bc92.projectsdk.constants.UserServiceConstants;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  //@formatter:off
  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    http
      .authorizeRequests()
      .antMatchers("/**")
      .authenticated();

//    http
//    .authorizeRequests()
//    .antMatchers("/**")
//    .permitAll();

    http.csrf().disable();
    http.logout().disable();

  }
//@formatter:on

  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName(UserServiceConstants.COOKIE_NAME);
    return serializer;
  }

}
