package refresh.onecake.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    fun findByUserId(userId:String): Member
    fun getByUserId(userId:String): Member
    fun existsByUserId(userId:String) : Boolean
    fun findMemberTypeById(id:Long) : MemberType
}