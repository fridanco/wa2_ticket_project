package it.polito.wa2.g17.ticketcatalogueservice.exceptions

class TicketPurchaseNotPermittedException : RuntimeException("User is not eligible to buy this type of ticket")