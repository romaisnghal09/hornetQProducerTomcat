package com.demo.hornetq.producer.configuration;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

@Configuration
@EnableJms
public class ProducerMessagingConfig {

	private static final Logger LOGGER = LogManager.getLogger(ProducerMessagingConfig.class);

	private static final String javaNamingFactoryInitial = "org.jboss.naming.remote.client.InitialContextFactory";

	@Value("${hornetq.servers.provider.url}")
	private String jmsNamingProviderUrl;

	@Value("${jms.naming.provider.port}")
	private String jmsNamingProviderPort;

	@Value("${spring.jms.jndi-name}")
	private String jmsConnectionFactoryName;

	@Value("${jms.username}")
	private String securityPrincipal;

	@Value("${jms.password}")
	private String securityCredentials;
	
	@Value("${jms.queue.jndi}")
	private String hornetQueue;
	
	/**
	 * This method is used to configure the JNDI factory using JNDI template.
	 * @return
	 */
	@Bean(name = "jndiConnectionFactory")
	public JndiObjectFactoryBean jndiConnectionFactory() {
		LOGGER.info("jndiConnectionFactory start .....");
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		Properties environment = new Properties();
		JndiTemplate jndiTemplate = new JndiTemplate();
		try {
			LOGGER.info("jndiConnectionFactory Try to connect Remote for jmsNamingProviderUrl ---- " + jmsNamingProviderUrl);
			//jndiObjectFactoryBean.getJndiTemplate().lookup(jmsNamingProviderUrl);
			if (StringUtils.isNotBlank(javaNamingFactoryInitial)) {
				environment.put(Context.INITIAL_CONTEXT_FACTORY, javaNamingFactoryInitial.trim());
			}

			if (StringUtils.isNotBlank(jmsNamingProviderUrl)) {
				environment.put(Context.PROVIDER_URL, jmsNamingProviderUrl.trim());
			}
			environment.put(Context.SECURITY_PRINCIPAL, securityPrincipal.trim());
			environment.put(Context.SECURITY_CREDENTIALS, securityCredentials.trim());
			jndiTemplate.setEnvironment(environment);
			jndiTemplate.lookup(hornetQueue);
			jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
			LOGGER.info("Remote jms naming provider url : " + jmsNamingProviderUrl);
		} catch (NamingException e) {
			LOGGER.error("Remote is not available trying to connect with local. " + e.getMessage());
			if (StringUtils.isNotBlank(javaNamingFactoryInitial)) {
				environment.put(Context.INITIAL_CONTEXT_FACTORY, javaNamingFactoryInitial.trim());
			}
			LOGGER.info("jmsNamingProviderPort-----" + jmsNamingProviderPort);
			if (StringUtils.isNotBlank(jmsNamingProviderUrl)) {
				environment.put(Context.PROVIDER_URL, "http-remoting://localhost:" + jmsNamingProviderPort);
				LOGGER.info("Local jms naming provider url : : http-remoting://localhost:" + jmsNamingProviderPort);

			}

			environment.put(Context.SECURITY_PRINCIPAL, securityPrincipal.trim());
			environment.put(Context.SECURITY_CREDENTIALS, securityCredentials.trim());
			jndiTemplate.setEnvironment(environment);
			jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
		}
		jndiObjectFactoryBean.setJndiName(jmsConnectionFactoryName);
		jndiObjectFactoryBean.setResourceRef(true);
		LOGGER.info("jndiConnectionFactory end .....");
		return jndiObjectFactoryBean;
	}

	/**
	 * This method is used to configure the JMS CachingConnectionFactory using
	 * jndiConnectionFactory.
	 * 
	 * @param jndiConnectionFactory
	 * @return
	 */
	@Bean(name = "jmsConnectionFactory")
	@Primary
	public CachingConnectionFactory jmsConnectionFactory(
			@Qualifier("jndiConnectionFactory") JndiObjectFactoryBean jndiConnectionFactory) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiConnectionFactory.getObject();
		cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}

	/**
	 * This method is used to configure JMS template using jmsConnectionFactory.
	 * 
	 * @param jmsConnectionFactory
	 * @return
	 */
	@Bean(name = "jmsTemplate")
	public JmsTemplate jmsTemplate(@Qualifier("jmsConnectionFactory") CachingConnectionFactory jmsConnectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
		return jmsTemplate;
	}
}
