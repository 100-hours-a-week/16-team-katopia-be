package katopia.fitcheck.service.search;

public final class LikeEscapeHelper {

    private LikeEscapeHelper() {
    }

    public static String escape(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
