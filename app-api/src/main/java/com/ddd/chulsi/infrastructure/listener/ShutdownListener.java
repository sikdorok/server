package com.ddd.chulsi.infrastructure.listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Component
public class ShutdownListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {}
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 버려진 연결 스레드를 중지하여 정리
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
