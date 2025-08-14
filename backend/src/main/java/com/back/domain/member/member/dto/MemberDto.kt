package com.back.domain.member.member.dto

import com.back.domain.member.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Getter
import java.time.LocalDateTime

@Getter
data class MemberDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val name: String,

    @field:JsonProperty("isAdmin")
    val admin: Boolean,
    val profileImageUrl: String
) {

    constructor(member: Member) : this(
        id = member.id,
        createDate = member.createDate,
        modifyDate = member.modifyDate,
        name = member.name,
        admin = member.isAdmin,
        profileImageUrl = member.profileImgUrlOrDefault
    )
}
