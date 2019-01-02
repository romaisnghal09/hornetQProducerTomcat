package com.demo.hornetq.producer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProducerController {

	/** LOGGER */
	private static final Logger LOGGER = LogManager.getLogger(ProducerController.class);
	
	@Value("${jms.producer.queue.jndi}")
	private String hornetQueue;

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@GetMapping(value = "/jms/{subscriberId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String jms(final HttpServletRequest request, final HttpServletResponse response,
			@PathVariable("subscriberId") String subscriberId) {
		String msg = "Message was not sent to hornetQ :  "+ subscriberId;
		try {

			jmsTemplate.convertAndSend(hornetQueue, subscriberId);
			msg = "message was sent to hornetQ successfully :  "+ subscriberId;
		} catch (Exception e) {
			LOGGER.error(msg);
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info(msg);
		return msg;
	}
	
	
}
