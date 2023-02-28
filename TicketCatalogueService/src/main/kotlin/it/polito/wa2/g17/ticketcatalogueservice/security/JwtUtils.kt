package it.polito.wa2.g17.ticketcatalogueservice.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.ticketcatalogueservice.Role
import it.polito.wa2.g17.ticketcatalogueservice.dtos.UserJwtDTO
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.InvalidJwtException
import it.polito.wa2.g17.ticketcatalogueservice.utils.AdminPermissionsUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtUtils : InitializingBean {

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    //Encode JWT with this secret and select Base64 encoded
    @Value("\${authenticationJwtSecret}")
    lateinit var secret : String

    lateinit var hmacKey : Key

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    fun validateJwt(authToken : String) : Boolean{
        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKey)
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

    fun getDetailsJwt(authToken: String) : UserJwtDTO {
        validateJwt(authToken)
        if (!validateJwt(authToken)){
            throw InvalidJwtException()
        }

        val validatedJwt : Jws<Claims>

        try {
            //JWT validation
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(authToken)
        }
        catch (e : Exception){
            throw InvalidJwtException()
        }

        val nickname = validatedJwt.body.subject
        val role : Role
        val permissions : String
        try{
            role = Role.valueOf(validatedJwt.body["roles"] as String)
            if(role == Role.ROLE_CUSTOMER){
                permissions = adminPermissionsUtils.serializeCustomerPermissions()
            }
            else{
                permissions = validatedJwt.body["permissions"] as String
            }
        }
        catch (e: Exception){
            throw InvalidJwtException()
        }

        return UserJwtDTO(nickname, role, permissions)
    }

}