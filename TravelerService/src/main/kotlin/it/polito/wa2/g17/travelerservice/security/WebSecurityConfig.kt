package it.polito.wa2.g17.travelerservice.security

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
                .antMatchers("/traveler/user/**").hasAuthority("ROLE_CUSTOMER")
                .antMatchers("/traveler/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/traveler/super_admin/**").hasAuthority("ROLE_SUPER_ADMIN").and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().formLogin().disable()
        }

    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}
