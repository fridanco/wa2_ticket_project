package it.polito.wa2.g17.turnstileservice.repository

import it.polito.wa2.g17.turnstileservice.entities.Turnstile
import org.springframework.data.repository.CrudRepository

interface TurnstileRepository : CrudRepository<Turnstile, Long> {

    abstract fun findTurnstileById(id :Long) : Turnstile?

}