package it.polito.wa2.g17.ticketvalidationservice.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.ticketvalidationservice.Role
import it.polito.wa2.g17.ticketvalidationservice.dtos.TurnstileJwtDTO
import it.polito.wa2.g17.ticketvalidationservice.dtos.UserJwtDTO
import it.polito.wa2.g17.ticketvalidationservice.exceptions.InvalidJwtException
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtUtils : InitializingBean {

    //Encode JWT with this secret and select Base64 encoded
    @Value("\${authenticationJwtSecret}")
    lateinit var secretUser : String

    @Value("\${turnstileAuthenticationJwtSecret}")
    lateinit var secretTurnstile : String

    lateinit var hmacKeyUser : Key
    lateinit var hmacKeyTurnstile : Key

    override fun afterPropertiesSet() {
        hmacKeyUser = SecretKeySpec(Base64.getDecoder().decode(secretUser), SignatureAlgorithm.HS256.jcaName)
        hmacKeyTurnstile = SecretKeySpec(Base64.getDecoder().decode(secretTurnstile), SignatureAlgorithm.HS256.jcaName)
    }

    fun validateJwtForUser(authToken : String) : Boolean{
        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKeyUser)
                .build()
                .parseClaimsJws(authToken)

            //Get token expiration from JWT body (payload) claims & perform checks
            val expiration = validatedJwt.body.expiration
            if (expiration == null || expiration.before(Date())) {
                throw Exception("Expired JWT")
            }
        }
        catch (e: Exception){
            println(e.message)
            return false
        }
        return true
    }

    fun validateJwtForTurnstile(authToken : String) : Boolean{
        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKeyTurnstile)
                .build()
                .parseClaimsJws(authToken)

            //Get token expiration from JWT body (payload) claims & perform checks
            val expiration = validatedJwt.body.expiration
            if (expiration == null || expiration.before(Date())) {
                throw Exception("Expired JWT")
            }
        }
        catch (e: Exception){
            println(e.message)
            return false
        }
        return true
    }

    fun getDetailsJwtUser(authToken: String) : UserJwtDTO {
        if (!validateJwtForUser(authToken)){
            throw InvalidJwtException()
        }

        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKeyUser)
                .build()
                .parseClaimsJws(authToken)
        }
        catch (e : Exception){
            throw InvalidJwtException()
        }

        val nickname = validatedJwt.body.subject
        var role : Role
        try{
            role = Role.valueOf(validatedJwt.body["roles"].toString())
        }
        catch (e: Exception){
            throw InvalidJwtException()
        }

        return UserJwtDTO(nickname, role)
    }

    fun getDetailsJwtTurnstile(authToken: String) : TurnstileJwtDTO {
        if (!validateJwtForTurnstile(authToken)){
            throw InvalidJwtException()
        }

        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKeyTurnstile)
                .build()
                .parseClaimsJws(authToken)
        }
        catch (e : Exception){
            throw InvalidJwtException()
        }

        val id : Long
        val zid: String
        try{
            id = validatedJwt.body["id"].toString().toLong()
            zid = validatedJwt.body["zone"].toString()
        }
        catch (e: Exception){
            throw InvalidJwtException()
        }

        return TurnstileJwtDTO(id, zid)
    }

}