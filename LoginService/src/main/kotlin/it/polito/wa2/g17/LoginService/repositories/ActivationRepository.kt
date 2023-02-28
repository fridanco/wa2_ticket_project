package it.polito.wa2.g17.LoginService.repositories

import it.polito.wa2.g17.LoginService.entities.Activation
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ActivationRepository : CrudRepository<Activation, String> {

    @Query(value = "SELECT * FROM activation WHERE provisional_id=?1", nativeQuery = true)
    fun findByUuidOrNull(provisional_id : UUID) : Activation?

    @Modifying
    @Query(value = "UPDATE activation SET counter=counter-1 WHERE provisional_id=?1", nativeQuery = true)
    fun decrementAttemptCounter(provisional_id : UUID)

    @Query(value = "SELECT * FROM activation WHERE deadline < current_timestamp ", nativeQuery = true)
    fun getExpired() : List<Activation>?

    @Modifying
    @Query(value = "DELETE FROM activation WHERE provisional_id=?1", nativeQuery = true)
    fun deleteByUuid(provisional_id : UUID)

}