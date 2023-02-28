package it.polito.wa2.g17.travelerservice.repositories

import it.polito.wa2.g17.travelerservice.entities.UserDetails
import org.springframework.data.repository.CrudRepository

interface UserDetailsRepository : CrudRepository<UserDetails, String> {

}
