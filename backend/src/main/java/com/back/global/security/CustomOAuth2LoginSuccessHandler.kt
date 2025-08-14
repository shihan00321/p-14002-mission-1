package com.back.global.security

import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class CustomOAuth2LoginSuccessHandler(
    private val memberService: MemberService,
    private val rq: Rq,
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val actor = rq.actorFromDb

        val accessToken = memberService.genAccessToken(actor)

        rq.apply {
            setCookie("apiKey", actor.apiKey)
            setCookie("accessToken", accessToken)
        }

        val redirectUrl = request.getParameter("state")
            ?.let { encoded ->
                runCatching {
                    val decoded = Base64.getUrlDecoder().decode(encoded)
                    String(decoded, StandardCharsets.UTF_8)
                }.getOrNull()
            }
            ?.substringBefore('#')
            ?.takeIf { it.isNotBlank() }
            ?: "/"

        rq.sendRedirect(redirectUrl)
    }
}
