package com.back.domain.home.home.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import lombok.SneakyThrows
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

@RestController
@Tag(name = "HomeController", description = "홈 컨트롤러")
class HomeController {
    @GetMapping(produces = [MediaType.TEXT_HTML_VALUE])
    @Operation(summary = "메인 페이지")
    fun main(): String {
        val localHost = InetAddress.getLocalHost()

        return """
                <h1>API 서버</h1>
                <p>Host Name: ${localHost.getHostName()}</p>
                <p>Host Address: ${localHost.getHostAddress()}</p>
                <div>
                    <a href="/swagger-ui/index.html">API 문서로 이동</a>
                </div>
                
                """.trimMargin()
    }

    @GetMapping("/session")
    @Operation(summary = "세션 확인")
    fun session(session: HttpSession): Map<String, Any> {
        return session.attributeNames
            .toList()
            .associateWith { session.getAttribute(it) }
    }
}
