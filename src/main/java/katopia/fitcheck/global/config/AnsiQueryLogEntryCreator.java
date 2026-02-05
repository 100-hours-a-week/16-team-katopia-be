package katopia.fitcheck.global.config;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;

import java.util.Locale;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnsiQueryLogEntryCreator extends DefaultQueryLogEntryCreator {
    public AnsiQueryLogEntryCreator() {
    }

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    private static final Pattern KEYWORDS = Pattern.compile(
            "(?i)\\b(select|update|insert|delete|from|join|where|having|limit|on|distinct|left|right|inner|outer|group\\s+by|order\\s+by)\\b"
    );

    @Override
    public String getLogEntry(ExecutionInfo execInfo,
                              List<QueryInfo> queryInfoList,
                              boolean writeDataSourceName,
                              boolean writeConnectionId,
                              boolean writeIsolation) {
        String queries = queryInfoList.stream()
                .map(this::mergeParams)
                .map(this::formatQuery)
                .collect(Collectors.joining("; "));
        String elapsedSeconds = String.format("%.3f", execInfo.getElapsedTime() / 1000.0);
        return "[Query](" + elapsedSeconds + "s) " + queries;
    }

    @Override
    protected String formatQuery(String query) {
        String formatted = super.formatQuery(query);
        Matcher matcher = KEYWORDS.matcher(formatted);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String keyword = matcher.group(1);
            matcher.appendReplacement(buffer, colorize(keyword));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String mergeParams(QueryInfo queryInfo) {
        String query = queryInfo.getQuery();
        var argsList = queryInfo.getQueryArgsList();
        if (argsList == null || argsList.isEmpty()) {
            return query;
        }
        // Pick first arg set (non-batch)
        var args = argsList.getFirst();
        if (args == null || args.isEmpty()) {
            return query;
        }
        String merged = query;
        for (Object value : args.values()) {
            merged = merged.replaceFirst("\\?", Matcher.quoteReplacement(formatValue(value)));
        }
        return merged;
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String text = value.toString().replace("'", "''");
        return "'" + text + "'";
    }

    private String colorize(String keyword) {
        String upper = keyword.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
        String color = switch (upper) {
            case "SELECT" -> GREEN;
            case "UPDATE" -> YELLOW;
            case "INSERT" -> BLUE;
            case "DELETE" -> RED;
            case "FROM", "JOIN", "WHERE", "HAVING", "LIMIT", "ON", "DISTINCT", "LEFT", "RIGHT", "INNER", "OUTER", "GROUP BY", "ORDER BY" ->
                    CYAN;
            default -> RESET;
        };
        return color + keyword + RESET;
    }
}
