package it.polito.wa2.g17.travelerservice.security

import it.polito.wa2.g17.travelerservice.exceptions.InvalidAuthorizationHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        try {
            val authorizationHeader = request.getHeader("Authorization")
            val jwt : String


            if (authorizationHeader.isNotEmpty() && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7, authorizationHeader.length)
            }
            else{
                throw InvalidAuthorizationHeader()
            }
            if (SecurityContextHolder.getContext().authentication == null) {
                val userJwt = jwtUtils.getDetailsJwt(jwt)

                val authentication = UsernamePasswordAuthenticationToken(
                    userJwt,
                    null,
                    listOf(SimpleGrantedAuthority(userJwt.role.toString()))
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        catch (e: Exception){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Invalid JWT - authentication failed")
            println(e.message)
            return
        }
        filterChain.doFilter(request, response)
    }


}