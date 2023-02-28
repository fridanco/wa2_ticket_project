package it.polito.wa2.g17.eurekadiscoveryservice.security

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
class WebSecurity : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity?) {
        http?.let {
            it.csrf()
            .disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
        }

    }
}