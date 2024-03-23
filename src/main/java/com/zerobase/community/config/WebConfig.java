package com.zerobase.community.config;

import com.zerobase.community.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginCheckInterceptor())
        .order(1)
        // 인터셉터를 적용할 URL 패턴 지정
        .addPathPatterns("/**")
        .excludePathPatterns("/",
            "/member/join", "/member/login",
            "/board/detail/**", "/board/list",
            "/board/search/**",
            "/css/**", "/*.ico", "/error");
  }

}
