package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.security.SecurityUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class Rq(
    private val req: HttpServletRequest,
    private val resp: HttpServletResponse,
    private val memberService: MemberService,
) {
    val actor: Member
        get() = (SecurityContextHolder.getContext()
            ?.authentication
            ?.principal as? SecurityUser)
            ?.let { Member(it.id, it.username, it.nickname) }
            ?: throw IllegalStateException("인증된 사용자가 없습니다.")

    val actorFromDb: Member
        get() = memberService.findById(actor.id)

    fun getHeader(name: String, defaultValue: String): String {
        return req.getHeader(name) ?: defaultValue
    }

    fun setHeader(name: String, value: String) {
        resp.setHeader(name, value)
    }

    fun getCookieValue(name: String, defaultValue: String): String =
        req.cookies
            ?.firstOrNull { it.name == name }
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?: defaultValue

    fun setCookie(
        name: String,
        value: String?,
        domain: String = "localhost",
        path: String = "/",
        maxAge: Int = if (value.isNullOrBlank()) 0 else 60 * 60 * 24 * 365,
        httpOnly: Boolean = true,
        secure: Boolean = true,
        sameSite: String = "Strict"
    ) {
        Cookie(name, value.orEmpty()).apply {
            this.path = path
            this.domain = domain
            isHttpOnly = httpOnly
            this.secure = secure
            setAttribute("SameSite", sameSite)
            this.maxAge = maxAge
        }.also {resp.addCookie(it) }
    }

    fun deleteCookie(name: String) {
        setCookie(name, null)
    }

    fun sendRedirect(url: String) {
        resp.sendRedirect(url)
    }
}
