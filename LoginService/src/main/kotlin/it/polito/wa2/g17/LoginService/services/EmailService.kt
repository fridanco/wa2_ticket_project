package it.polito.wa2.g17.LoginService.services

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import java.util.*


@Service
class EmailService : InitializingBean {

    lateinit var emailSender: JavaMailSender

    @Value("\${spring.mail.host}")
    lateinit var emailHost : String

    @Value("\${spring.mail.port}")
    var emailPort : Int = 587

    @Value("\${spring.mail.username}")
    lateinit var emailUsername : String

    @Value("\${spring.mail.password}")
    lateinit var emailPassword : String

    override fun afterPropertiesSet() {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = emailHost
        mailSender.port = emailPort
        mailSender.username = emailUsername
        mailSender.password = emailPassword
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"

        emailSender = mailSender
    }

    fun sendEmail(subject: String,text:String,targetEmail:String){
        val msg = SimpleMailMessage()
        msg.setSubject(subject)
        msg.setText(text)
        msg.setTo(targetEmail)
        emailSender.send(msg)
    }

}