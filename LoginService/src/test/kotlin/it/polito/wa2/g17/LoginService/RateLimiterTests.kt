package it.polito.wa2.g17.LoginService

import it.polito.wa2.g17.LoginService.dtos.RegistrationDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimiterTests : InitializingBean {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    lateinit var baseUrlRegister : String
    lateinit var baseUrlValidate : String

    override fun afterPropertiesSet() {
        baseUrlRegister = "http://localhost:$port/user/register"
        baseUrlValidate = "http://localhost:$port/user/validate"
    }

    @Test
    fun testRegistrationRateLimiter() {
        val request = HttpEntity(RegistrationDTO("", "noreply.wa2g17@gmail.com","Abcdef123!?"))
        var response: HttpStatus = HttpStatus.BAD_REQUEST
        for(i in 1..100){
            if(restTemplate.postForEntity<Unit>(baseUrlRegister,request).statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                response = HttpStatus.TOO_MANY_REQUESTS
                break
            }
        }
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, response)
    }

    @Test
    fun testValidationRateLimiter() {
        var response: HttpStatus = HttpStatus.BAD_REQUEST
        val provisional_id = UUID.randomUUID()
        val activation_code = UUID.randomUUID()
        for(i in 1..100){
            if(restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${provisional_id}&activation_code=${activation_code}",Unit::class.java).statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                response = HttpStatus.TOO_MANY_REQUESTS
                break
            }
        }
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, response)
    }
}
