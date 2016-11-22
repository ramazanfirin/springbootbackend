package guru.springframework.test.concurrent;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;





import ayonix.FaceID;
import guru.springframework.concurrent.FaceMatchResultDTO;
import guru.springframework.test.ByteArraySender;

public class TestCallableTask implements Callable<FaceMatchResultDTO>{
	
	String url;
	byte[] query;
	
public TestCallableTask(String url , byte[] query) {
		this.url=url;
		this.query = query;
	}



@Override
public FaceMatchResultDTO call() throws Exception {
	return ByteArraySender.send(url, query);
	//return 
}

public static int sort(float[] scores){
	
	float value = Float.MIN_VALUE;
	int index = Integer.MIN_VALUE;
	
	for(int i =0;i<scores.length;i++) {
	
	            if(scores[i] > value) {
	            	value = scores[i];

	            	index = i;

	            }
	
	        }

	return index;
}


}
