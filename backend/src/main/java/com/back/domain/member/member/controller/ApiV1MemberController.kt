package com.back.domain.member.member.controller


import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.dto.MemberWithUsernameDto
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1MemberController(
    private val memberService: MemberService,
    private val rq: Rq
) {

    data class MemberJoinReqBody(
        @field:NotBlank
        @field:Size(min = 2, max = 30)
        val username: String,

        @field:NotBlank
        @field:Size(min = 2, max = 30)
        val password: String,

        @field:NotBlank
        @field:Size(min = 2, max = 30)
        val nickname: String
    )

    @PostMapping
    @Transactional
    @Operation(summary = "가입")
    fun join(@RequestBody @Valid reqBody: MemberJoinReqBody): RsData<MemberDto> {
        val member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname)
        return RsData(
            resultCode = "201-1",
            msg = "${member.name}님 환영합니다. 회원가입이 완료되었습니다.",
            data = MemberDto(member)
        )
    }

    data class MemberLoginReqBody(
        @field:NotBlank
        @field:Size(min = 2, max = 30)
        val username: String,

        @field:NotBlank
        @field:Size(min = 2, max = 30)
        val password: String
    )

    data class MemberLoginResBody(
        val item: MemberDto,
        val apiKey: String,
        val accessToken: String
    )


    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인")
    fun login(@RequestBody @Valid reqBody: MemberLoginReqBody): RsData<MemberLoginResBody> {
        val member = memberService.findByUsername(reqBody.username)

        memberService.checkPassword(member, reqBody.password)
        val accessToken = memberService.genAccessToken(member)

        rq.apply {
            setCookie("apiKey", member.apiKey)
            setCookie("accessToken", accessToken)
        }

        return RsData(
            resultCode = "200-1",
            msg = "${member.name}님 환영합니다.",
            data = MemberLoginResBody(
                item = MemberDto(member),
                apiKey = member.apiKey,
                accessToken = accessToken
            )
        )
    }

    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(): RsData<Void> {
        rq.apply {
            deleteCookie("apiKey")
            deleteCookie("accessToken")
        }
        return RsData(resultCode = "200-1", msg = "로그아웃 되었습니다.")
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    @Operation(summary = "내 정보")
    fun me(): MemberWithUsernameDto {
        val actor = rq.actorFromDb
        return MemberWithUsernameDto(actor)
    }
}
