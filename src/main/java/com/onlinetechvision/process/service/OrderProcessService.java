package com.onlinetechvision.process.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.onlinetechvision.model.Order;

/**
 * Order Process Service Activator listens processChannel and logs incoming Order messages.
 * 
 * @author Eren Avsarogullari
 * @since 15 Jan 2015
 * @version 1.0.0
 *
 */
public class OrderProcessService implements ProcessService<Order> {
	
	private final Logger logger;
	private final static long SLEEP_DURATION = 1_000;
	
	public OrderProcessService(Logger logger) {
        this.logger = logger;
    }

    @Override
    @ServiceActivator
	public void process(Message<Order> message) {
		try {
			Thread.sleep(SLEEP_DURATION);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		logger.debug("Node 1 - Received Message : " + message.getPayload());
	}
	
}
