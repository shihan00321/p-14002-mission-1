package com.back.global.initData

import com.back.domain.member.member.service.MemberService
import com.back.domain.post.post.service.PostService
import com.back.global.app.CustomConfigProperties
import com.back.global.app.CustomConfigProperties.NotProdMember
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
class NotProdInitData(
    private val postService: PostService,
    private val memberService: MemberService,
    private val customConfigProperties: CustomConfigProperties,
) {
    @Autowired
    @Lazy
    private lateinit var self: NotProdInitData

    @Bean
    fun notProdInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            self.work1()
            self.work2()
        }
    }

    @Transactional
    fun work1() {
        if (memberService.count() > 0) return

        val memberSystem = memberService.join("system", "1234", "시스템")
        memberSystem.modifyApiKey(memberSystem.username)

        val memberAdmin = memberService.join("admin", "1234", "관리자")
        memberAdmin.modifyApiKey(memberAdmin.username)

        val memberUser1 = memberService.join("user1", "1234", "유저1")
        memberUser1.modifyApiKey(memberUser1.username)

        val memberUser2 = memberService.join("user2", "1234", "유저2")
        memberUser2.modifyApiKey(memberUser2.username)

        val memberUser3 = memberService.join("user3", "1234", "유저3")
        memberUser3.modifyApiKey(memberUser3.username)

        customConfigProperties.notProdMembers.forEach(Consumer { notProdMember: NotProdMember ->
            val socialMember = memberService.join(
                notProdMember.username,
                null,
                notProdMember.nickname,
                notProdMember.profileImgUrl
            )

            socialMember.modifyApiKey(notProdMember.apiKey)
        })
    }

    @Transactional
    fun work2() {
        if (postService.count() > 0) return

        val memberUser1 = memberService.findByUsername("user1")
        val memberUser2 = memberService.findByUsername("user2")
        val memberUser3 = memberService.findByUsername("user3")

        val post1 = postService.write(memberUser1, "제목 1", "내용 1")
        val post2 = postService.write(memberUser1, "제목 2", "내용 2")
        val post3 = postService.write(memberUser2, "제목 3", "내용 3")

        post1.addComment(memberUser1, "댓글 1-1")
        post1.addComment(memberUser1, "댓글 1-2")
        post1.addComment(memberUser2, "댓글 1-3")
        post2.addComment(memberUser3, "댓글 2-1")
        post2.addComment(memberUser3, "댓글 2-2")
    }
}