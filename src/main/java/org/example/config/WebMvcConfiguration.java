package org.example.config;

import org.example.common.properties.JwtProperties;
import org.example.common.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private JwtProperties jwtProperties;

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor(stringRedisTemplate, jwtProperties))
				.excludePathPatterns(
						"/comment/**",
						"/swagger-resources/**",
						"/webjars/**",
						"/v2/**",
						"/swagger-ui.html",
						"/site/config",
						"/site/banners",
						"/doctor/list",
						"/department/all",
						"/error",
						"/home/**"
				);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

}
