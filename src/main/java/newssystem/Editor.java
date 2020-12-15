package newssystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Editor{
	
	private ConcurrentMap<Integer, News> published = null;
	//private ArrayList<News> news = null;
	
	public Editor(ConcurrentHashMap<Integer, News> p)
	{
		this.published = p;
	}

	public static void main(String args[]) {
		
		int newsCounter = 0;
		ConcurrentHashMap<Integer, News> p = new ConcurrentHashMap<Integer, News>();
		Editor editor = new Editor(p);
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
        	
        	channel.exchangeDeclare("news", "topic");
        	channel.exchangeDeclare("readers", "fanout");
        	
        	String queueName = channel.queueDeclare().getQueue();
        	
        	channel.queueBind(queueName, "readers", "");
        	
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("A user received the notification.");
            };
            
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
            
            Scanner in = new Scanner(System.in);
        	
        	while(true)
        	{
        		/*Date date = new Date();
        		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");*/
        		
        		System.out.println("You want to add, edit or delete an article?");
        		
        		String option = in.nextLine();
        		
        		if(option.toLowerCase().equals("add"))
        		{
        		
        			System.out.println("Enter the name of the author:");
        			
        			String author = in.nextLine();
        			
        			System.out.println("Enter the category of the article:");
        			
        			String category = in.nextLine();
        			
        			System.out.println("Enter the source of the article:");
        			
        			String source  = in.nextLine();
        			
        			Date date = new Date();
            		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        			
        			News news = new News(author, df.format(date), df.format(date), source, category);
	                
	                news.setArticleContent("Fake news");
	                
	                System.out.println(news.getRoutingKey());
	                
	                String message = Integer.toString(newsCounter) + "^" + news.getArticleContent();
	                
	                editor.published.put(Integer.valueOf(newsCounter), news);
	                
	                newsCounter++;
	                
	                channel.basicPublish("news", news.getRoutingKey(), null, message.getBytes(StandardCharsets.UTF_8));
	                
	                System.out.println(" [x] Sent '" + message + "'");
	                
	                try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	
        	
        		else if(option.toLowerCase().equals("edit"))
        		{
        			if(editor.published.isEmpty())
        			{
        				System.out.println("You have not published any news.");
        			}
        			else
        			{
        				System.out.println("These are the news you published until now:");
        				for(Entry<Integer, News> entry: editor.published.entrySet())
        				{
        					System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        				}
        				
        				System.out.println("Enter the number of the news you want to modify:");
        				String number = in.nextLine();
        				System.out.println("Enter the new content for the news:");
        				String newContent = in.nextLine();
        				
        				News toModify = editor.published.get(Integer.valueOf(number));
        				toModify.setArticleContent(newContent);
        				
        				Date date = new Date();
                		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                		
                		toModify.setLastEdit(df.format(date));
                		
                		editor.published.put(Integer.valueOf(number), toModify);
                		
                		String message = number + "^" + toModify.getArticleContent();
                		
                		channel.basicPublish("news", toModify.getRoutingKey(), null, message.getBytes(StandardCharsets.UTF_8));
        			}
        		}
        		else if(option.toLowerCase().equals("delete")) 
        		{
        			System.out.println("These are the news you published until now:");
    				for(Entry<Integer, News> entry: editor.published.entrySet())
    				{
    					System.out.println(entry.getKey() + ": " + entry.getValue().toString());
    				}
    				
    				System.out.println("Enter the number of the news you want to delete:");
    				String number = in.nextLine();
    				
    				News toDelete = editor.published.get(Integer.valueOf(number));
    				
    				editor.published.remove(Integer.valueOf(number));
    				
    				String message = number + "^" + "deleted";
    				
    				channel.basicPublish("news", toDelete.getRoutingKey(), null, message.getBytes(StandardCharsets.UTF_8));
        		}
        		else
        		{
        			System.out.println("Sorry, the option you entered does not exist.");
        		}
        	}
            
        } catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
