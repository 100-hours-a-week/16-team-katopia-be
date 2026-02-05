package katopia.fitcheck.global.aop;

import katopia.fitcheck.dto.search.SearchResultCount;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
public class SearchLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SearchLogAspect.class);

    @Around("@annotation(searchLog)")
    public Object logSearch(ProceedingJoinPoint joinPoint, SearchLog searchLog) throws Throwable {
        long startNs = System.nanoTime();
        String query = extractQuery(joinPoint.getArgs());
        int queryLen = StringUtils.hasText(query) ? query.length() : 0;

        try {
            Object result = joinPoint.proceed();
            int resultCount = extractResultCount(result);
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
            log.info("[SEARCH] type={} queryLen={} results={} elapsedMs={}",
                    searchLog.value(),
                    queryLen,
                    resultCount,
                    elapsedMs);
            return result;
        } catch (RuntimeException ex) {
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
            log.warn("[SEARCH] type={} queryLen={} elapsedMs={} error={}",
                    searchLog.value(),
                    queryLen,
                    elapsedMs,
                    ex.getClass().getSimpleName());
            throw ex;
        }
    }

    private String extractQuery(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof String str) {
                return str;
            }
        }
        return null;
    }

    private int extractResultCount(Object result) {
        if (result instanceof SearchResultCount count) {
            return count.resultCount();
        }
        return -1;
    }
}
