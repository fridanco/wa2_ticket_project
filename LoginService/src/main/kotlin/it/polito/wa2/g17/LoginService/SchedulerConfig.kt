package it.polito.wa2.g17.LoginService

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["scheduler.enabled"], matchIfMissing = true)
class SchedulerConfig : WebMvcConfigurer {
    private var interceptor: RateLimiterInterceptor = RateLimiterInterceptor()
    override fun addInterceptors(registry: InterceptorRegistry) {
        interceptor.let {
            registry.addInterceptor(it)
                .addPathPatterns("/user/**")
        }
    }
}