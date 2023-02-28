package it.polito.wa2.g17.LoginService.repositories

import it.polito.wa2.g17.LoginService.entities.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByNicknameAndEmail(nickname:String, email:String) : User?

    fun findByNickname(nickname:String) : User?

    fun findUserByEmail(email:String) : User?

    fun deleteUserById(id: Long)

    fun deleteUserByEmail(email: String)

    @Modifying
    @Query(value = "UPDATE users SET valid=TRUE WHERE id=?1", nativeQuery = true)
    fun setUserValid(id: Long)

}