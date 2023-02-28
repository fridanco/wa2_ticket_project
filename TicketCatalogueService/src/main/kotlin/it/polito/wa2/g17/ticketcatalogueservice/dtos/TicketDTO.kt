package it.polito.wa2.g17.ticketcatalogueservice.dtos

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TicketDTO(
  @NotNull
  @field:Size(min = 1)
  var ticket_id: String,

  @NotNull
  var price : Long,

  @NotNull
  @field:Size (min = 1)
  var type : String,


)