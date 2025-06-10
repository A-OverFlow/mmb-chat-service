package com.mumulbo.msaplayground.service
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "member-service", url = "\${member-service.url}")
interface MemberServiceClient {
    @GetMapping("/members/me")
    fun getMemberInfo(@RequestHeader("X-User-Id") userId: Long): MemberInfoDto
}

data class MemberInfoDto(
    val nickname: String,
    val email: String
)