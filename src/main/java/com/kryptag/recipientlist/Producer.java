/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.recipientlist;

import com.kryptag.rabbitmqconnector.RMQConnection;
import com.kryptag.rabbitmqconnector.RMQProducer;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author florenthaxha
 */
public class Producer extends RMQProducer{
    
    public Producer(ConcurrentLinkedQueue queue, RMQConnection rmq) {
        super(queue, rmq);
    }
    
    @Override
    public void run() {
       try {
            this.getRmq().putMessageInQueue(this.getQueue());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RMQProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
