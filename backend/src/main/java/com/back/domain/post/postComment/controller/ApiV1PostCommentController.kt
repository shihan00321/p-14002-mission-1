package com.back.domain.post.postComment.controller

import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.service.PostService
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postComment.entity.PostComment
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.RequiredArgsConstructor
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostCommentController", description = "API 댓글 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostCommentController(
    private val postService: PostService,
    private val rq: Rq
) {

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    fun getItems(
        @PathVariable postId: Int
    ): List<PostCommentDto> {
        val post: Post = postService.findById(postId)

        return post.comments
            .stream()
            .map { PostCommentDto(it) }
            .toList()
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    fun getItem(
        @PathVariable postId: Int,
        @PathVariable id: Int
    ): PostCommentDto {
        val post: Post = postService.findById(postId)

        val postComment: PostComment = post.findCommentById(id)

        return PostCommentDto(postComment)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    fun delete(
        @PathVariable postId: Int,
        @PathVariable id: Int
    ): RsData<Void> {
        val actor = rq.actor

        val post: Post = postService.findById(postId)

        val postComment: PostComment = post.findCommentById(id)

        postComment.checkActorCanDelete(actor)

        postService.deleteComment(post, postComment)

        return RsData(
            resultCode = "200-1",
            msg = "${id}번 댓글이 삭제되었습니다."
        )
    }


    @JvmRecord
    data class PostCommentModifyReqBody(
        val content: @NotBlank @Size(min = 2, max = 100) String
    )

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    fun modify(
        @PathVariable postId: Int,
        @PathVariable id: Int,
        @RequestBody reqBody: @Valid PostCommentModifyReqBody
    ): RsData<Void> {
        val actor = rq.actor

        val post: Post = postService.findById(postId)

        val postComment: PostComment = post.findCommentById(id)

        postComment.checkActorCanModify(actor)

        postService.modifyComment(postComment, reqBody.content)

        return RsData(
            resultCode = "200-1",
            msg = "${id}번 댓글이 수정되었습니다."
        )
    }


    @JvmRecord
    data class PostCommentWriteReqBody(
        val content: @NotBlank @Size(min = 2, max = 100) String
    )

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    fun write(
        @PathVariable postId: Int,
        @RequestBody reqBody: @Valid PostCommentWriteReqBody
    ): RsData<PostCommentDto?> {
        val actor = rq.actor
        val post: Post = postService.findById(postId)

        val postComment: PostComment = postService.writeComment(actor, post, reqBody.content)

        // 트랜잭션 끝난 후 수행되어야 하는 더티체킹 및 여러가지 작업들을 지금 당장 수행해라.
        postService.flush()

        return RsData(
            resultCode = "201-1",
            msg = "${postComment.id}번 댓글이 작성되었습니다.",
            data = PostCommentDto(postComment)
        )
    }
}
