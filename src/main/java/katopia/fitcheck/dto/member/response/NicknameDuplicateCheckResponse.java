package katopia.fitcheck.dto.member.response;

public record NicknameDuplicateCheckResponse(boolean isDuplicated) {
    public static NicknameDuplicateCheckResponse of(boolean duplicated) {
        return new NicknameDuplicateCheckResponse(duplicated);
    }
}
