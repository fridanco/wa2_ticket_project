package it.polito.wa2.g17.LoginService

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import io.github.bucket4j.local.LocalBucket
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



class RateLimiterInterceptor : HandlerInterceptorAdapter() {
    private val tokenBucket: LocalBucket = Bucket4j.builder()
        .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(1))))
        .build()
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val probe = tokenBucket.tryConsumeAndReturnRemaining(1)
        return if (probe.isConsumed) {
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            true
        } else {
            val waitForRefill = probe.nanosToWaitForRefill / 1_000_000_000
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
            response.sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "You have exhausted your API Request Quota"
            )
            false
        }
    }
}