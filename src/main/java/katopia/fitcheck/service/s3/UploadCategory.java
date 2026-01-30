package katopia.fitcheck.service.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadCategory {
    PROFILE(1, "profiles"),
    POST(3, "posts"),
    VOTE(5, "votes");

    private final int maxCount;
    private final String folder;
}
