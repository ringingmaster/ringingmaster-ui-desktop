package com.concurrentperformance.ringingmaster.fxui.desktop.beanfactory;

import com.concurrentperformance.util.beanfactory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Simple Spring implementation. This just hides Spring from the rest of the application.
 *
 * @author Lake
 */
public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public <T> T build(Class<T> type) {
		return applicationContext.getBean(type);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
