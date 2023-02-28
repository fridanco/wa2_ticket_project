package it.polito.wa2.g17.turnstileservice.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    fun authenticationJwtTokenFilter(): JwtAuthenticationTokenFilter? {
        return JwtAuthenticationTokenFilter()
    }

    override fun configure(http: HttpSecurity?) {
        http?.let {
            it.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
            it.authorizeRequests()
                .antMatchers("/turnstile/embedded/**").hasAuthority("ROLE_EMBEDDED")
                .antMatchers("/turnstile/admin/**").hasAuthority("ROLE_ADMIN").and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().formLogin().disable()
        }

    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}