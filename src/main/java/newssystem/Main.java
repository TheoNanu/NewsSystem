package newssystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<Integer, News> p = new ConcurrentHashMap<Integer, News>();
		ArrayList<News> news = new ArrayList<News>();
		/*ArrayList<String> categories = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();*/
		
		/*categories.add("Sport");
		categories.add("Politics");
		authors.add("Alex");
		sources.add("Reuters");*/
		
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		
		News n1 = new News("Alex", df.format(date), df.format(date), "Reuters", "Sport");
		News n2 = new News("Adi", df.format(date), df.format(date), "Reuters", "Financial");
		News n3 = new News("Alex", df.format(date), df.format(date), "Reuters", "Social");
		News n4 = new News("Alex", df.format(date), df.format(date), "Reuters", "Politics");
		
		news.add(n1);
		news.add(n2);
		news.add(n3);
		news.add(n4);
		
		/*Editor editor = new Editor(p, news);
		
		
		Thread t1 = new Thread(editor);
		
		t1.start();
		
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try {
			t2.join();
			System.out.println("Reader thread ended");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
