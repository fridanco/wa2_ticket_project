package it.polito.wa2.g17.LoginService.services

import it.polito.wa2.g17.LoginService.Role
import it.polito.wa2.g17.LoginService.dtos.*
import it.polito.wa2.g17.LoginService.entities.Activation
import it.polito.wa2.g17.LoginService.entities.User
import it.polito.wa2.g17.LoginService.exceptions.ActivationCodeNotMatchException
import it.polito.wa2.g17.LoginService.exceptions.DeadlineExpiredException
import it.polito.wa2.g17.LoginService.exceptions.ProvisionalIdNotExist
import it.polito.wa2.g17.LoginService.exceptions.UserIsNotUnique
import it.polito.wa2.g17.LoginService.repositories.ActivationRepository
import it.polito.wa2.g17.LoginService.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class UserService {


    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder


    fun userRegistration(registrationDTO: RegistrationDTO): RegistrationResponseDTO {

        if(userRepository.findByNicknameAndEmail(registrationDTO.nickname, registrationDTO.email) != null){
           throw UserIsNotUnique()
        }

        val userToSave = User().apply {
            nickname=registrationDTO.nickname
            email=registrationDTO.email
            password=passwordEncoder.encode(registrationDTO.password)
            valid=false
        }
        val user = userRepository.save(userToSave)

        val activationToSave = Activation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            user,
            5,
            Date(System.currentTimeMillis()+7200000)
        )
        val activation = activationRepository.save(activationToSave)

        val emailBody = "Hi ${user.nickname}\n" +
                "Please click the link below to activate you account:\n\n" +
                "http://localhost:8080/user/validate?provisional_id=${activation.provisionalId}&activation_code=${activation.activationCode}\n\n" +
                "Best regards\n" +
                "WA2 - Group 17\n"
        val scope = CoroutineScope(Dispatchers.IO + Job())
        scope.launch {
                emailService.sendEmail("Account activation",emailBody, registrationDTO.email)
        }

        return RegistrationResponseDTO(activation.provisionalId, user.email)
    }

    @Transactional(noRollbackFor = [ActivationCodeNotMatchException::class, DeadlineExpiredException::class])
    fun userValidation(validationDTO: ValidationDTO): ValidationResponseDTO {

        val activation = activationRepository.findByUuidOrNull(validationDTO.provisionalId) ?: throw ProvisionalIdNotExist()

        //If activation code does not match reduce counter
        if(activation.activationCode!=validationDTO.activationCode){
            //If counter reaches 0 delete account
            if(activation.counter-1<=0){
                activationRepository.deleteByUuid(validationDTO.provisionalId)
                userRepository.deleteUserById(activation.user.id)
            }
            else{
                activationRepository.decrementAttemptCounter(activation.provisionalId)
            }
            throw ActivationCodeNotMatchException()
        }
        //If validation request received after the validation deadline delete provisional account
        if(Date(System.currentTimeMillis()).after(activation.deadline)){
            activationRepository.deleteByUuid(validationDTO.provisionalId)
            userRepository.deleteUserById(activation.user.id)
            throw DeadlineExpiredException()
        }

        userRepository.setUserValid(activation.user.id)
        activationRepository.deleteByUuid(validationDTO.provisionalId)

        return ValidationResponseDTO(activation.user.id, activation.user.nickname, activation.user.email)

    }

    fun adminCreateUserAccount(registrationDTO: RegistrationDTO): ValidationResponseDTO {

        if(userRepository.findByNicknameAndEmail(registrationDTO.nickname, registrationDTO.email) != null){
            throw UserIsNotUnique()
        }

        var user = User().apply {
            nickname = registrationDTO.nickname
            email = registrationDTO.email
            password = passwordEncoder.encode(registrationDTO.password)
            role = Role.ROLE_CUSTOMER
            valid = true
            disabled = false
        }

        user = userRepository.save(user)

        return ValidationResponseDTO(user.id, user.nickname, user.email)

    }

    fun adminCreateAdminAccount(adminCreateAccountDTO: AdminCreateAccountDTO): ValidationResponseDTO {

        if(userRepository.findByNicknameAndEmail(adminCreateAccountDTO.nickname, adminCreateAccountDTO.email) != null){
            throw UserIsNotUnique()
        }

        var user = User().apply {
            nickname = adminCreateAccountDTO.nickname
            email = adminCreateAccountDTO.email
            password = passwordEncoder.encode(adminCreateAccountDTO.password)
            role = Role.ROLE_ADMIN
            loginServiceManageUsers = adminCreateAccountDTO.loginServiceManageUsers
            loginServiceManageAdmins = adminCreateAccountDTO.loginServiceManageAdmins
            turnstileServiceManageTurnstile = adminCreateAccountDTO.turnstileServiceManageTurnstile
            travelerServiceManageTravelers = adminCreateAccountDTO.travelerServiceManageTravelers
            travelerServiceManageReports = adminCreateAccountDTO.travelerServiceManageReports
            ticketCatalogueServiceManageTickets = adminCreateAccountDTO.ticketCatalogueServiceManageTickets
            ticketCatalogueServiceManageOrders = adminCreateAccountDTO.ticketCatalogueServiceManageOrders
            paymentServiceManageTransactions = adminCreateAccountDTO.paymentServiceManageTransactions
            valid = true
            disabled = false
        }

        user = userRepository.save(user)

        return ValidationResponseDTO(user.id, user.nickname, user.email)

    }


    @Scheduled(cron = "@hourly")
    fun pruningExpired() {
        val expiredActivations = activationRepository.getExpired()
        if(expiredActivations != null){
            activationRepository.deleteAll(expiredActivations)
            userRepository.deleteAll(expiredActivations.map { it.user })
        }
    }
}