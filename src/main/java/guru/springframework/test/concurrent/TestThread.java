package guru.springframework.test.concurrent;

public class TestThread extends Thread{
	String url;
	byte[] query;
	
	
	
	public TestThread(String url,byte[] query) {
		super();
		this.url = url;
		this.query = query;
	}



	public void run(){
		
	}
}
