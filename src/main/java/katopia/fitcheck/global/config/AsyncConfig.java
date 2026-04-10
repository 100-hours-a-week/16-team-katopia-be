package katopia.fitcheck.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationSendExecutor")
    public TaskExecutor notificationSendExecutor() {
        ExecutorService executor = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("notify-send-", 0).factory()
        );
        return new TaskExecutorAdapter(executor);
    }
}
