package com.onlinetechvision.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import com.onlinetechvision.integration.OrderGateway;
import com.onlinetechvision.model.Order;

/**
 * Application Class runs the application by initializing application context 
 * and sends order requests to messaging system.
 * 
 * @author Eren Avsarogullari
 * @since 15 Jan 2015
 * @version 1.0.0
 *
 */
@Configuration
public class SheduledTask {

	private final static int MESSAGE_LIMIT = 1_000;
	private final static int ORDER_LIST_SIZE = 10;
	private final static long SLEEP_DURATION = 50;
	
    @Autowired
    OrderGateway orderGateway;
	
	/**
     * Starts the application
     *
     * @param  String[] args
     *
     */
    @Scheduled(fixedRate = 60000)
    public void test01() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					int firstIndex = 0, lastIndex = ORDER_LIST_SIZE;
					while(lastIndex <= MESSAGE_LIMIT) {
						Message<List<Order>> message = MessageBuilder.withPayload(getOrderList(firstIndex, lastIndex)).build();
						orderGateway.processOrderRequest(message);
						firstIndex += ORDER_LIST_SIZE;
						lastIndex += ORDER_LIST_SIZE;
						Thread.sleep(SLEEP_DURATION);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
	
	/**
     * Creates a sample order list and returns.
     *
     * @return order list
     */
    private static List<Order> getOrderList(final int firstIndex, final int lastIndex) {
    	List<Order> orderList = new ArrayList<>(lastIndex);
    	for(int i = firstIndex; i < lastIndex; i++) {
    		orderList.add(new Order(i, "Sample_Order_" + i));
    	}
    	
        return orderList;
    }
	
}
