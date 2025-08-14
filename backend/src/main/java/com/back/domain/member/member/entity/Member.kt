package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import lombok.NoArgsConstructor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@Entity
class Member(
    id: Int,
    @field:Column(unique = true) val username: String,
    var password: String? = null,
    var nickname: String,
    @field:Column(unique = true) var apiKey: String,
    var profileImgUrl: String?
) : BaseEntity(id) {
    constructor(id: Int, username: String, nickname: String) : this(
        id,
        username,
        null,
        nickname,
        "",
        null
    )

    constructor(username: String, password: String?, nickname: String, profileImgUrl: String?) : this(
        0,
        username,
        password,
        nickname,
        UUID.randomUUID().toString(),
        profileImgUrl
    )

    val profileImgUrlOrDefault: String
        get() = profileImgUrl ?: "https://placehold.co/600x600?text=U_U"

    val name: String
        get() = nickname

    val isAdmin: Boolean
        get() = username in listOf("system", "admin")

    val authoritiesAsStringList: List<String>
        get() {
            val authorities: MutableList<String> = ArrayList()
            if (isAdmin) authorities.add("ROLE_ADMIN")
            return authorities
        }

    val authorities: Collection<GrantedAuthority>
        get() = authoritiesAsStringList.map(::SimpleGrantedAuthority)

    fun modify(nickname: String, profileImgUrl: String?) {
        this.nickname = nickname
        this.profileImgUrl = profileImgUrl
    }

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }
}
