package it.polito.wa2.g17.travelerservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g17.travelerservice.dtos.*
import it.polito.wa2.g17.travelerservice.entities.UserDetails
import it.polito.wa2.g17.travelerservice.entities.report.Report
import it.polito.wa2.g17.travelerservice.entities.report.ReportEntry
import it.polito.wa2.g17.travelerservice.exceptions.ReportNotFoundException
import it.polito.wa2.g17.travelerservice.exceptions.UserEmptyProfileException
import it.polito.wa2.g17.travelerservice.kafka.entities.received.KafkaValidationReportResponse
import it.polito.wa2.g17.travelerservice.kafka.entities.sent.KafkaValidationReportRequest
import it.polito.wa2.g17.travelerservice.kafka.entities.sent.TicketPurchasedInfo
import it.polito.wa2.g17.travelerservice.repositories.ReportRepository
import it.polito.wa2.g17.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.g17.travelerservice.repositories.UserDetailsRepository
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class AdminService {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Qualifier("topic_travelerServiceToTicketValidationService")
    @Autowired
    lateinit var travelerServiceToTicketValidationService: NewTopic

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var reportRepository: ReportRepository

    fun enrollAdmin(authenticationUsername: String, adminProfileDto: UserProfileDto) {
        val adminProfile = userDetailsRepository.findByIdOrNull(authenticationUsername)
        if(adminProfile==null){
            userDetailsRepository.save(UserDetails(authenticationUsername, adminProfileDto.name, adminProfileDto.address, adminProfileDto.dateOfBirth, adminProfileDto.telephoneNumber, mutableListOf()))
        }
        else{
            adminProfile.apply {
                this.name = adminProfileDto.name
                this.address = adminProfileDto.address
                this.dateOfBirth = adminProfileDto.dateOfBirth
                this.telephoneNumber = adminProfileDto.telephoneNumber
            }
            userDetailsRepository.save(adminProfile)
        }
    }

    fun getTravelers(): List<UserDetailsDto> {
        return userDetailsRepository.findAll().map { it.toDTO() }
    }

    fun getTravelerProfile(nickname: String) : UserDetailsDto {
        return userDetailsRepository.findByIdOrNull(nickname)?.toDTO() ?: throw UserEmptyProfileException()
    }

    fun getTravelerTickets(nickname: String) : List<TicketPurchasedDto> {
        val profile = userDetailsRepository.findByIdOrNull(nickname) ?: return listOf()
        return ticketPurchasedRepository.findAllByUserDetails(profile).map { it.toDTO() }
    }

    fun generateReportUser(userReportRequestDTO: UserReportRequestDTO): ReportResponseDTO {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val startDateMillis = sdf.parse(userReportRequestDTO.startTime).time
        val endDateMillis = sdf.parse(userReportRequestDTO.endTime).time

        val userDetails = userDetailsRepository.findByIdOrNull(userReportRequestDTO.userID)
            ?: throw UserEmptyProfileException()

        val userTickets = ticketPurchasedRepository.findAllByUserDetailsAndIatBetween(userDetails, startDateMillis, endDateMillis)

        val report = reportRepository.save(
            Report(
                UUID.randomUUID(),
                "PROCESSING",
                null,
                null,
                null,
                listOf(),
                userDetails
            )
        )

        sendMessageKafkaToTicketValidationService(
            KafkaValidationReportRequest(
                report.id,
                userTickets.map {
                    TicketPurchasedInfo(
                        it.id,
                        it.jws,
                        Date(it.iat),
                        it.ticketType
                    )
                }
            )
        )

        return ReportResponseDTO(report.id)
    }

    fun generateReport(reportRequestDTO: ReportRequestDTO) : ReportResponseDTO {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val startDateMillis = sdf.parse(reportRequestDTO.startTime).time
        val endDateMillis = sdf.parse(reportRequestDTO.endTime).time

        val userTickets = ticketPurchasedRepository.findAllByIatBetween(startDateMillis, endDateMillis)

        val report = reportRepository.save(
            Report(
                UUID.randomUUID(),
                "PROCESSING",
                null,
                null,
                null,
                listOf(),
                null
            )
        )

        sendMessageKafkaToTicketValidationService(
            KafkaValidationReportRequest(
                report.id,
                userTickets.map {
                    TicketPurchasedInfo(
                        it.id,
                        it.jws,
                        Date(it.iat),
                        it.ticketType
                    )
                }
            )
        )

        return ReportResponseDTO(report.id)
    }

    fun getReport(reportID: UUID) : ReportDTO {

        return reportRepository.findByIdOrNull(reportID)?.toDTO() ?: throw ReportNotFoundException()

    }

    @KafkaListener(topics = ["ticketValidationService_travelerService"], groupId = "it.polito.wa2.travelerservice")
    fun getTicketValidations(message: String){

        var ticketValidationReport: KafkaValidationReportResponse? = null

        try {
            val mapper = jacksonObjectMapper()
            ticketValidationReport = mapper.readValue(message, KafkaValidationReportResponse::class.java)
        }
        catch (e: Exception) {
            println(e.message)
            return
        }

        try{
            val report = reportRepository.findByIdOrNull(ticketValidationReport.reportID) ?: return

            if(!ticketValidationReport.succeeded){
                println("Ticket Validation response is FAILED")
                report.apply {
                    this.status = "FAILED"
                }
                reportRepository.save(report)
                return
            }

            println("Ticket Validation response is SUCCEEDED")

            val reportEntriesList = ticketValidationReport.ticketList.map {
                ReportEntry(
                    0,
                    it.ticketID,
                    it.ticketGeneratedTimestamp,
                    it.ticketValidatedTimestamp,
                    it.ticketValidatedTurnstileID,
                    it.ticketValidatedZone
                )
            }

            val numTickets = reportEntriesList.size
            val numValidatedTickets = reportEntriesList.count { it.ticketValidatedTimestamp!=null }

            report.apply {
                this.reportEntries = reportEntriesList
                this.status = "COMPLETED"
                this.reportGeneratedTimestamp = Date(System.currentTimeMillis())
                this.numTickets = numTickets
                this.numValidations = numValidatedTickets
            }

            reportRepository.save(report)

            println("Report marked as completed and updated")

        }
        catch (e: Exception){
            println(e.message)

            val report = reportRepository.findByIdOrNull(ticketValidationReport.reportID) ?: return

            report.status = "FAILED"

            reportRepository.save(report)

            println("Report marked as FAILED")

        }
    }

    fun sendMessageKafkaToTicketValidationService(payload: KafkaValidationReportRequest){
        val message: Message<KafkaValidationReportRequest> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, travelerServiceToTicketValidationService.name())
            .build()
        kafkaTemplate.send(message)
        println("Kafka message to Ticket Validation Service sent with success")
    }



}
