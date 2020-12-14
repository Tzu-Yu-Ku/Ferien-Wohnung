/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fewodre;

import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableSalespoint
public class FeWoDre {

	private static final String LOGIN_ROUTE = "/login";
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public static void main(String[] args) {
		SpringApplication.run(FeWoDre.class, args);
	}

	@Configuration
	static class FeWoDreWebConfiguration implements WebMvcConfigurer {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController(LOGIN_ROUTE).setViewName("/login");
		}
	}


	@Configuration
	static class WebSecurityConfiguration extends SalespointSecurityConfiguration {
		public String doSomething(final HttpServletRequest request) {
			return request.getHeader("referer");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			http.authorizeRequests().antMatchers("/**").permitAll().and()
					.formLogin()
					.loginPage(LOGIN_ROUTE)
					.successHandler(new AuthenticationSuccessHandler() {
						@Override
						public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
							System.out.println(httpServletRequest.getHeader("referer"));
							if (httpServletRequest.getHeader("referer").equals("http://localhost:8080/newhost") || httpServletRequest.getHeader("referer").equals("http://localhost:8080/neweventemployee") || httpServletRequest.getHeader("referer").equals("http://localhost:8080/activatetenants")) {
							System.out.println("true");
							httpServletResponse.sendRedirect(httpServletRequest.getHeader("referer"));
							}
							else {httpServletResponse.sendRedirect("http://localhost:8080/holidayhomes");}
						}
					})
					.loginProcessingUrl(LOGIN_ROUTE)
					.and()
					.logout()
					.logoutUrl("/logout")
					.logoutSuccessUrl("/holidayhomes");
		}
	}
}
