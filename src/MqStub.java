import com.ibm.mq.jms.*;

import javax.jms.*;

public class MqStub {
    public static void main(String[] args) {
        try {
            MQQueueConnection mcConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqSession;
            MQQueue mqIn;
            MQQueue mqOut;
            MQQueueReceiver mqReceiver;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1414);

            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mcConn = (MQQueueConnection) mqCF.createConnection();
            mqSession = (MQQueueSession) mcConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqSession.createQueue("MQ.IN");
            mqReceiver = (MQQueueReceiver) mqSession.createReceiver(mqIn);

            mqOut = (MQQueue) mqSession.createQueue("MQ.OUT");
            mqSender = (MQQueueSender) mqSession.createSender(mqOut);



            javax.jms.MessageListener listener =  new MessageListener() {
                @Override
                public void onMessage(Message msg) {
                    System.out.println("Got message");
                    if(msg instanceof TextMessage) {
                        try {
                            TextMessage tMsg = (TextMessage) msg;
                            String msgText =  tMsg.getText();
                            System.out.println(msgText);
                            mqSender.send(tMsg);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            mqReceiver.setMessageListener(listener);
            mcConn.start();
            System.out.println("Stub Started");

        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
