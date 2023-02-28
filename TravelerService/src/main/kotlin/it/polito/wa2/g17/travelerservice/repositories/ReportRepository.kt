package it.polito.wa2.g17.travelerservice.repositories;

import it.polito.wa2.g17.travelerservice.entities.report.Report
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ReportRepository : CrudRepository<Report, UUID> {
}