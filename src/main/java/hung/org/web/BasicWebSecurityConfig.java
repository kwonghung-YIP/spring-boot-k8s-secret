package hung.org.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile("basicAuth")
@Configuration
@EnableWebSecurity
public class BasicWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.debug(true);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		  .authorizeRequests()
		    .anyRequest()
		      .authenticated()
		  .and()
		    .httpBasic();
	}

	
}
