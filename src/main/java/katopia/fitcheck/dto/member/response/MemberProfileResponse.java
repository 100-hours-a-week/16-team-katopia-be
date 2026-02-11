package katopia.fitcheck.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.global.docs.SwaggerExamples;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileResponse(
    @Schema(description = SwaggerExamples.MEMBER_ID_DES, example = SwaggerExamples.MEMBER_ID_EXAMPLE)
    Long id,
    @Schema(description = "프로필 정보")
    MemberProfile profile,
    @Schema(description = "집계 정보")
    MemberAggregate aggregate,
    @Schema(description = "수정 시각", example = SwaggerExamples.TIMESTAMP_EXAMPLE)
    LocalDateTime updatedAt
) {
    public static MemberProfileResponse of(Member member) {
        return MemberProfileResponse.builder()
                .id(member.getId())
                .profile(MemberProfile.of(member))
                .aggregate(member.getAggregate())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
