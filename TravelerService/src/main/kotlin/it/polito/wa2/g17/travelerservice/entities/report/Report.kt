package it.polito.wa2.g17.travelerservice.entities.report

import it.polito.wa2.g17.travelerservice.entities.UserDetails
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "report")
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: UUID,
    @Column(nullable = false)
    var status: String,
    @Column(nullable = true)
    var reportGeneratedTimestamp: Date?,
    @Column(nullable = true)
    var numTickets: Int?,
    @Column(nullable = true)
    var numValidations: Int?,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "report_id")
    var reportEntries: List<ReportEntry>,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    var userDetails: UserDetails?,
)