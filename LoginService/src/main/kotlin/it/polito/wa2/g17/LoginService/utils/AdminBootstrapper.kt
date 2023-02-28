package it.polito.wa2.g17.LoginService.utils

import it.polito.wa2.g17.LoginService.Role
import it.polito.wa2.g17.LoginService.entities.User
import it.polito.wa2.g17.LoginService.repositories.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional

@Configuration
class AdminBootstrapper : InitializingBean {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional
    override fun afterPropertiesSet() {
        println("Admin bootstrapping procedure started...")
        bootstrapAdmin()
        println("Admin bootstrapping procedure ended...")
    }

    private fun bootstrapAdmin(){

        val user = userRepository.findByNickname("admin")

        if(user==null){
            val admin = User()
            userRepository.save(
                admin.apply {
                    nickname = "admin"
                    email = "admin@email.com"
                    password = "Admin123."
                    role = Role.ROLE_ADMIN
                    loginServiceManageUsers = true
                    loginServiceManageAdmins = true
                    turnstileServiceManageTurnstile = true
                    travelerServiceManageTravelers = true
                    travelerServiceManageReports = true
                    ticketCatalogueServiceManageTickets = true
                    ticketCatalogueServiceManageOrders = true
                    paymentServiceManageTransactions = true
                    valid = true
                    disabled = false
                }
            )
            println("Admin account CREATED: nickname:admin / pwd:Admin123.")
            return
        }

        println("Admin account ALREADY EXISTS: nickname:admin / pwd:Admin123.")

    }
}