package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import katopia.fitcheck.global.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface DevApiSpec {

    @Operation(summary = "개발용 회원 하드 삭제", description = "memberId 기준으로 회원 및 연관 데이터를 하드 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 사용자 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    ResponseEntity<Void> hardDeleteMember(
            @PathVariable("memberId") Long memberId
    );
}
