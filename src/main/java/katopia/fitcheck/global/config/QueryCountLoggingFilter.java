package katopia.fitcheck.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@ConditionalOnProperty(prefix = "app.datasource-proxy", name = "enabled", havingValue = "true")
public class QueryCountLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(QueryCountLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        QueryCountHolder.clear();
        long startNs = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            QueryCount count = QueryCountHolder.getGrandTotal();
            if (count != null && count.getTotal() > 0) {
                double elapsedSeconds = (System.nanoTime() - startNs) / 1_000_000_000.0;
                double querySeconds = count.getTime() / 1000.0;
                double sqlPct = elapsedSeconds > 0 ? (querySeconds / elapsedSeconds) * 100.0 : 0.0;
                log.info("[REQ] {} {}({}s, sql={}s, sqlPct={}%%): total={} select={} insert={} update={} delete={} other={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        String.format("%.3f", elapsedSeconds),
                        String.format("%.3f", querySeconds),
                        String.format("%.1f", sqlPct),
                        count.getTotal(),
                        count.getSelect(),
                        count.getInsert(),
                        count.getUpdate(),
                        count.getDelete(),
                        count.getOther());
            }
            QueryCountHolder.clear();
        }
    }
}
