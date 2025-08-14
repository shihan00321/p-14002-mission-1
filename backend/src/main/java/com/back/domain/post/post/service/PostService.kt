package com.back.domain.post.post.service

import com.back.domain.member.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.domain.post.postComment.entity.PostComment
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class PostService(
    private val postRepository: PostRepository
) {

    fun count() = postRepository.count()

    fun write(author: Member, title: String, content: String) = postRepository.save(Post(author, title, content))

    fun findById(id: Int) = postRepository.findById(id).orElseThrow { NoSuchElementException("Post not found: $id") }

    fun findAll(): List<Post> = postRepository.findAll()

    fun modify(post: Post, title: String, content: String) = post.modify(title, content)

    fun writeComment(author: Member, post: Post, content: String) = post.addComment(author, content)

    fun deleteComment(post: Post, postComment: PostComment) = post.deleteComment(postComment)

    fun modifyComment(postComment: PostComment, content: String) = postComment.modify(content)


    fun delete(post: Post) = postRepository.delete(post)

    fun findLatest() = postRepository.findFirstByOrderByIdDesc() ?: throw NoSuchElementException("No posts found")

    fun flush() = postRepository.flush()
}
