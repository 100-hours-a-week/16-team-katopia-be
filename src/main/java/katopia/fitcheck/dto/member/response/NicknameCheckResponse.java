package katopia.fitcheck.dto.member.response;

public record NicknameCheckResponse(
        boolean isAvailable
) {
    public static NicknameCheckResponse of(boolean duplicated) {
        return new NicknameCheckResponse(!duplicated);
    }
}
