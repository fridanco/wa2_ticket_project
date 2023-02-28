package it.polito.wa2.g17.LoginService

import it.polito.wa2.g17.LoginService.dtos.RegistrationDTO
import it.polito.wa2.g17.LoginService.dtos.RegistrationResponseDTO
import it.polito.wa2.g17.LoginService.repositories.ActivationRepository
import it.polito.wa2.g17.LoginService.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class DbT1ApplicationTests : InitializingBean {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    lateinit var baseUrlRegister : String
    lateinit var baseUrlValidate : String

    override fun afterPropertiesSet() {
        baseUrlRegister = "http://localhost:$port/user/register"
        baseUrlValidate = "http://localhost:$port/user/validate"
    }

    @Test
    fun testEmptyNickname() {
        val request = HttpEntity(RegistrationDTO("", "noreply.wa2g17@gmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidEmailFormat() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "wa2g17gmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testEmptyEmail() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "","Abcdef123!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testEmptyPassword() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com",""))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidPasswordNoUppercase() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com","abcdef123!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidPasswordNoDigit() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com","Abcdefgh!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidPasswordNoSymbol() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com","Abcdef123"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidPasswordWithWhitespace() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com","Abcdef123!? "))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testInvalidPasswordTooShort() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname", "noreply.wa2g17@gmail.com","Ab123!?"))
        val response = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testDuplicateUser() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname2", "mario.ramio@protonmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity(baseUrlRegister,request, RegistrationResponseDTO::class.java)
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        val response2 = restTemplate.postForEntity<Unit>(baseUrlRegister,request)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.statusCode)
    }

//    @Test
//   fun testEmptyProvisionalID() {
//        val response = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=&activation_code=${UUID.randomUUID()}", Unit::class.java)
//        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
//    }
//
//    @Test
//    fun testEmptyActivationCode() {
//        val response = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${UUID.randomUUID()}&activation_code=", Unit::class.java)
//        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
//    }

    @Test
    fun testInvalidProvisionalID() {
        val response = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${UUID.randomUUID()}&activation_code=${UUID.randomUUID()}", Unit::class.java)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun testInvalidActivationCode() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname3", "ustaidurres@protonmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity(baseUrlRegister,request, RegistrationResponseDTO::class.java)

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)

        val provisional_id = response.body!!.provisional_id

        val response2 = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${provisional_id}&activation_code=${UUID.randomUUID()}", Unit::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.statusCode)

        val activation = activationRepository.findByUuidOrNull(provisional_id)
        Assertions.assertNotNull(activation)
        Assertions.assertEquals(activation?.counter, 4)
    }

    @Test
    fun testInvalidActivationCode5Times() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname4", "noreply.wa2g17@gmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity(baseUrlRegister,request, RegistrationResponseDTO::class.java)

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)

        var response2 : Any
        val provisional_id = response.body!!.provisional_id
        val activation_code = UUID.randomUUID()

        for(i in 1..5){
            response2 = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${provisional_id}&activation_code=${activation_code}", Unit::class.java)
            Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.statusCode)
        }

        val activation = activationRepository.findByUuidOrNull(provisional_id)
        val user = userRepository.findUserByEmail("noreply.wa2g17@gmail.com")
        Assertions.assertNull(activation)
        Assertions.assertNull(user)
    }

    @Test
    fun testValidValidation() {
        val request = HttpEntity(RegistrationDTO("wa2g17_testNickname5", "mario.deda.97@hotmail.com","Abcdef123!?"))
        val response = restTemplate.postForEntity(baseUrlRegister,request, RegistrationResponseDTO::class.java)

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)

        val provisional_id = response.body!!.provisional_id
        var activation = activationRepository.findByUuidOrNull(provisional_id)
        val activation_code = activation!!.activationCode

        val response2 = restTemplate.getForEntity<Unit>("$baseUrlValidate?provisional_id=${provisional_id}&activation_code=${activation_code}", Unit::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, response2.statusCode)

        activation = activationRepository.findByUuidOrNull(provisional_id)
        val user = userRepository.findUserByEmail("mario.deda.97@hotmail.com")
        Assertions.assertNull(activation)
        Assertions.assertNotNull(user)
    }

}
