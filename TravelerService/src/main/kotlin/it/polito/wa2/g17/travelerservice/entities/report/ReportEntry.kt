package it.polito.wa2.g17.travelerservice.entities.report

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "report_entry")
class ReportEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long,
    @Column(nullable = false)
    val ticketID: UUID,
    @Column(nullable = false)
    val ticketGeneratedTimestamp: Date,
    @Column(nullable = true)
    val ticketValidatedTimestamp: Date?,
    @Column(nullable = true)
    val ticketValidatedTurnstileID: Long?,
    @Column(nullable = true)
    val ticketValidatedZone: String?,
    )