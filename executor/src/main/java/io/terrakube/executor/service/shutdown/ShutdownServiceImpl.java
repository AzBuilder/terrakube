package io.terrakube.executor.service.shutdown;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutdownServiceImpl implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void shutdownApplication(){
        ((ConfigurableApplicationContext) applicationContext).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
