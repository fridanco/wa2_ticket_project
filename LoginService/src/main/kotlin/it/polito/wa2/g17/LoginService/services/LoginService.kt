package it.polito.wa2.g17.LoginService.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.LoginService.Role
import it.polito.wa2.g17.LoginService.dtos.CredentialDTO
import it.polito.wa2.g17.LoginService.dtos.LoginResponseDTO
import it.polito.wa2.g17.LoginService.entities.User
import it.polito.wa2.g17.LoginService.exceptions.*
import it.polito.wa2.g17.LoginService.repositories.UserRepository
import it.polito.wa2.g17.LoginService.utils.AdminPermissionsUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class LoginService : InitializingBean {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @Value("\${authenticationJwtSecret}")
    lateinit var secret : String

    lateinit var hmacKey: Key

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    fun loginUser(credentialDTO: CredentialDTO) : Any? {
        val loginUser : User = userRepository.findByNickname(credentialDTO.nickname) ?: throw UserDoesNotExistException()

        if(!loginUser.valid){
            throw AccountNotValidatedException()
        }

        if(loginUser.disabled){
            throw AccountDisabledException()
        }

        if(!passwordEncoder.matches(credentialDTO.password,loginUser.password)) {
            throw PasswordDoesNotMatch()
        }

        when (loginUser.role) {
            Role.ROLE_CUSTOMER -> {
                return Jwts
                    .builder()
                    .setClaims(mapOf("sub" to loginUser.nickname, "roles" to loginUser.role))
                    .setIssuedAt(Date(System.currentTimeMillis()))
                    .setExpiration(Date(System.currentTimeMillis() + 3600000))    //1 hour validity
                    .signWith(hmacKey)
                    .compact()
            }
            Role.ROLE_ADMIN -> {
                val adminJwtClaims = mapOf(
                    "sub" to loginUser.nickname,
                    "roles" to loginUser.role,
                    "permissions" to adminPermissionsUtils.serializeAdminPermissions(loginUser)
                )

                return LoginResponseDTO(
                    Jwts.builder()
                        .setClaims(adminJwtClaims)
                        .setIssuedAt(Date(System.currentTimeMillis()))
                        .setExpiration(Date(System.currentTimeMillis() + 3600000))    //1 hour validity
                        .signWith(hmacKey)
                        .compact()
                )
            }
            else -> {
                throw UserRoleDoesNotExistException()
            }
        }


    }

}