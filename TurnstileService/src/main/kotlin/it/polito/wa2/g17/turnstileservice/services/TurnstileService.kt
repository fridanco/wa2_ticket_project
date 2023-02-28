package it.polito.wa2.g17.turnstileservice.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.turnstileservice.dtos.*
import it.polito.wa2.g17.turnstileservice.entities.Turnstile
import it.polito.wa2.g17.turnstileservice.exceptions.NetworkCallFailedException
import it.polito.wa2.g17.turnstileservice.exceptions.TicketValidationFailedException
import it.polito.wa2.g17.turnstileservice.exceptions.TurnstileDisabledException
import it.polito.wa2.g17.turnstileservice.exceptions.TurnstileNotFoundException
import it.polito.wa2.g17.turnstileservice.repository.TurnstileRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Service
class TurnstileService : InitializingBean {
    @Autowired
    lateinit var turnstileRepository: TurnstileRepository

    @Autowired
    lateinit var loadBalancedRestTemplate: RestTemplate

    @Value("\${turnstileAuthenticationJwtSecret}")
    lateinit var secretTurnstile : String

    lateinit var hmacKeyTurnstile: Key

    override fun afterPropertiesSet() {
        hmacKeyTurnstile = SecretKeySpec(Base64.getDecoder().decode(secretTurnstile), SignatureAlgorithm.HS256.jcaName)
    }

    fun addTurnstile(turnstileAddDTO: TurnstileAddDTO) : TurnstileCreatedResponseDTO {

        val turnstileCompleted = Turnstile(0, "NotGeneratedYet",turnstileAddDTO.zid,turnstileAddDTO.disabled)
        val turnWithId = turnstileRepository.save(turnstileCompleted)

        val jwtExpiration = System.currentTimeMillis()+3155692600000 as Long

        val jwtString : String  = Jwts
            .builder()
            .setClaims(mapOf("id" to turnWithId.id, "zone" to turnWithId.zid))
            .setExpiration(Date(jwtExpiration))
            .signWith(hmacKeyTurnstile)
            .compact()

        turnWithId.jwt = jwtString
        turnstileRepository.save(turnWithId)
        return TurnstileCreatedResponseDTO(jwtString)
    }

    fun getTurnstile(id: Long): Turnstile? {
        return turnstileRepository.findTurnstileById(id)
    }

    fun getTurnstileDTO(id: Long) : TurnstileDTO{
        return turnstileRepository.findByIdOrNull(id)?.toDTO() ?: throw TurnstileNotFoundException(id)
    }

    fun putStateTurnstile(id: Long, state : Boolean) : Boolean{
        val turnstile = getTurnstile(id) ?: return false
        turnstile.disabled = state
        turnstileRepository.save(turnstile)
        return true
    }

    fun deleteTurnstile (id: Long){
        turnstileRepository.deleteById(id)
    }

    fun validateTicketTurnstile(ticket: TicketTurnstileDTO, turnstileJwtDTO: TurnstileJwtDTO) : Boolean{
        val turnstile = getTurnstile(turnstileJwtDTO.id)

        if(turnstile==null){
            return false
        }

        if(turnstile.disabled){
            throw TurnstileDisabledException()
        }

        val url = "http://Ticket-Validation-Service/ticket/validation/embedded/validate"

        val headers = HttpHeaders()
        headers.setBearerAuth(turnstile.jwt)

        val body = mapOf<String, String>(
            "ticketJwt" to ticket.jws_ticket,
        )

        val request = HttpEntity<Map<String, String>>(body, headers)

        //LOGIN CALL
        try {
            val validationResponse = loadBalancedRestTemplate.postForEntity(url, request, String::class.java)
            if(validationResponse.body == "true"){
                return true
            }
            throw TicketValidationFailedException(ticket.jws_ticket, validationResponse.body.toString())
        }
        //4xx HTTP errors are caught here
        catch (exception: Exception) {
            println(exception.message)
            throw NetworkCallFailedException(url, exception.message)
        }

    }

    fun getTurnstiles() : List<TurnstileDTO> {
        return turnstileRepository.findAll().map { it.toDTO() }
    }
}