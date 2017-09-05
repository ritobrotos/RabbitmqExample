package consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class Receiver {
  private final static String QUEUE_NAME = "test-queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setUsername("guest");
    factory.setPassword("guest");
    Connection connection = factory.newConnection();
    final Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    channel.basicQos(1);
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
        try {
          doWork(message);
        } catch(InterruptedException e) {
          System.out.println("There is an interrupted exception");
        } finally {
          System.out.println(" [x] Done");
//          channel.basicAck(envelope.getDeliveryTag(), false);
        }
      }
    };
    boolean autoAck = false;
    channel.basicConsume(QUEUE_NAME, autoAck, consumer);
  }

  private static void doWork(String task) throws InterruptedException {
    for (char ch: task.toCharArray()) {
      if (ch == 'M') {
        Thread.sleep(3000);
      }
    }
  }
}
