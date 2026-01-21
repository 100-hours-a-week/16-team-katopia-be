package katopia.fitcheck.member.dto;

public record NicknameDuplicateCheckResponse(boolean isDuplicated) {
    public static NicknameDuplicateCheckResponse of(boolean duplicated) {
        return new NicknameDuplicateCheckResponse(duplicated);
    }
}
