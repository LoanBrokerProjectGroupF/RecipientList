/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.recipientlist;

import com.google.gson.Gson;
import com.kryptag.rabbitmqconnector.MessageClasses.AggregatorMessage;
import com.kryptag.rabbitmqconnector.MessageClasses.RuleMessage;
import com.kryptag.rabbitmqconnector.RMQConnection;
import com.kryptag.rabbitmqconnector.RMQConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author florenthaxha
 */
public class Consumer extends RMQConsumer{
    private final RMQConnection aggregatorCon;
    
    public Consumer(RMQConnection aggregatorCon, ConcurrentLinkedQueue q, RMQConnection rmq) {
        super(q, rmq);
        this.aggregatorCon = aggregatorCon;
    }

    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            doWork();
        }
    }
    
    public void doWork(){
        Gson g = new Gson();
        RMQConnection rmq = this.getRmq();
        aggregatorCon.createConnection();
        if (!this.getQueue().isEmpty()) {
            RuleMessage rmsg = g.fromJson(this.getQueue().remove().toString(), RuleMessage.class);
            //rmsg.getBankNames().forEach(bankname -> this.getRmq().sendMessage(g.toJson(rmsg.getCmsg())));
            for (int i = 0; i < rmsg.getBankNames().size(); i++) {
                    String bankTranslatorExchange = rmsg.getBankNames().get(i);
                    rmq.setQueuename(bankTranslatorExchange);
                    rmq.createConnection();
                    rmq.sendMessage(g.toJson(rmsg.getCmsg()));
            }
            sendToAggregator(rmsg);
        }
    }
    
    private void sendToAggregator(RuleMessage rmsg){
        AggregatorMessage amsg = new AggregatorMessage(rmsg.getCmsg().getSsn(), rmsg.getBankNames().size());
        aggregatorCon.sendMessage(amsg.toString());
    }
}
