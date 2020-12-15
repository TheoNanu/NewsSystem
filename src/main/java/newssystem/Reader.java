package newssystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Reader{
	private ArrayList<String> categories = null;
	private ArrayList<String> dates = null;
	private ArrayList<String> authors = null;
	private ArrayList<String> sources = null;
	private HashMap<Integer, String> messages = new HashMap<Integer, String>();
	//private String messageBody = null;
	//private String routingKey = null;
	
	public Reader() {
		this.categories = new ArrayList<String>();
		this.dates = new ArrayList<String>();
		this.authors = new ArrayList<String>();
		this.sources = new ArrayList<String>();
	}
	
	public void subscribeByCategory(String category) {
		this.categories.add(category);
	}
	
	public void subscribeByDate(String date) {
		this.dates.add(date);
	}
	
	public void subscribeByAuthor(String author) {
		this.authors.add(author);
	}
	
	public void subscribeBySource(String source) {
		this.sources.add(source);
	}
	
	public void processMessage(String messageBody, String routingKey) {
		
		String content[] = messageBody.split("\\^");
		
		if(content[1].equals("deleted"))
		{
			if(this.messages.containsKey(Integer.parseInt(content[0])))
			{
				System.out.println("One of the articles you were interested in got deleted:");
				System.out.println(this.messages.get(Integer.parseInt(content[0])));
				this.messages.remove(Integer.parseInt(content[0]));
			}
		}
		else
		{
			if(this.messages.containsKey(Integer.parseInt(content[0])))
			{
				this.messages.replace(Integer.parseInt(content[0]), content[1]);
				printNews("edited", routingKey, content[1]);
			}
			else
			{
				this.messages.put(Integer.parseInt(content[0]), content[1]);
				printNews("new", routingKey, content[1]);
			}
		}
	}
	
	public void printNews(String action, String routingKey, String body)
	{	
		if(action.equals("edited"))
			System.out.println("One of your news got edited!");
		else
			System.out.println("New article!");
		
		 String[] routes = routingKey.split("\\.");
         System.out.println("Category: " + routes[1]);
         System.out.println("From: " + routes[0]);
         System.out.println("Source: " + routes[4]);
         System.out.println("Publication time: " + routes[3]);
         System.out.println("Last edit: " + routes[2]);
         System.out.println(body);
         
         System.out.println();
	}
	
	public static void main(String args[]) throws IOException
	{
		Reader reader = new Reader();
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        final Channel channel = connection.createChannel();

		String queueName = null;
		try {
			channel.exchangeDeclare("news", "topic");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			queueName = channel.queueDeclare().getQueue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the categories you are interested in:");
        String input = in.nextLine();
        
        String tokens[] = input.split(" ");
        
        for (int x = 0; x < tokens.length; x++) {
            reader.subscribeByCategory(tokens[x]);
        }
        
        System.out.println("Enter the authors you want to hear from:");
        input = in.nextLine();
        
        tokens = input.split(" ");
        
        for (int x = 0; x < tokens.length; x++) {
            reader.subscribeByAuthor(tokens[x]);
        }
        
        System.out.println("Enter the publication dates of the articles you want to receive:");
        input = in.nextLine();
        
        tokens = input.split(" ");
        
        for (int x = 0; x < tokens.length; x++) {
            reader.subscribeByDate(tokens[x]);
        }
        
        System.out.println("Enter the sources you trust the most:");
        input = in.nextLine();
        
        tokens = input.split(" ");
        
        for (int x = 0; x < tokens.length; x++) {
            reader.subscribeBySource(tokens[x]);
        }
        
        if(reader.categories.get(0).equals(""))
        	reader.categories.set(0, "*");
        
        if(reader.dates.get(0).equals(""))
        { 
        	reader.dates.set(0, "*");
        }
        
        if(reader.authors.get(0).equals(""))
        	reader.authors.set(0, "*");
        
        if(reader.sources.get(0).equals(""))
        	reader.sources.set(0, "*");
        
        for(String category: reader.categories)
        {
        	for(String date: reader.dates)
        	{
        		for(String author: reader.authors)
        		{
        			for(String source: reader.sources)
        			{
        				try {
        					String bindingKey = author + "." + category + "." + "*" + "." + date + "." + source;
        					System.out.println(bindingKey);
							channel.queueBind(queueName, "news", bindingKey);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
        		}
        	}
        	
        }
        
        System.out.println("Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String rKey = delivery.getEnvelope().getRoutingKey();
            
            reader.processMessage(message, rKey);
            
            channel.basicPublish("readers", "", null, "news read".getBytes(StandardCharsets.UTF_8));
        };
        try {
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}


