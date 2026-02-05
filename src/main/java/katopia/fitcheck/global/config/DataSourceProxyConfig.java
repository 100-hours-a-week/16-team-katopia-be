package katopia.fitcheck.global.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "app.datasource-proxy", name = "enabled", havingValue = "true")
public class DataSourceProxyConfig {

    @Value("${app.datasource-proxy.log-queries:false}")
    private boolean logQueries;

    @Value("${app.datasource-proxy.ansi:true}")
    private boolean ansi;

    @Bean
    public BeanPostProcessor dataSourceProxyBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof DataSource dataSource)) {
                    return bean;
                }
                if (beanName.contains("proxy")) {
                    return bean;
                }
                ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create(dataSource).countQuery();
                if (logQueries) {
                    SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
                    listener.setLogLevel(SLF4JLogLevel.INFO);
                    listener.setQueryLogEntryCreator(new AnsiQueryLogEntryCreator(ansi));
                    builder.listener(listener);
                }
                return builder.build();
            }
        };
    }
}
