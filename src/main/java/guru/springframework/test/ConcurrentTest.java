package guru.springframework.test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ayonix.AynxFace;
import ayonix.AynxImage;
import ayonix.FaceID;
import guru.springframework.concurrent.CallableTask;
import guru.springframework.concurrent.FaceMatchResultDTO;
import guru.springframework.concurrent.ResultDto;
import guru.springframework.test.concurrent.TestCallableTask;


public class ConcurrentTest {
	static String[]  url = new String[5]; 
	
	static int  parallelizm=1;
	static ExecutorService executorService = Executors.newFixedThreadPool(parallelizm);
	
	
	public static void main(String[] args) throws Exception {
		prepareURLList();
	FaceID sdk = new FaceID("C:\\Program Files (x86)\\Ayonix\\FaceID\\data\\engine");
	
	AynxImage image = sdk.LoadImage("c://Users//ETR00529//Desktop//ramazan.png");
	AynxFace[] faces = sdk.DetectFaces(image);
	AynxFace face = faces[0];
	sdk.PreprocessFace(face);
	byte[] afid = sdk.CreateAfid(face);
	System.out.println(afid);
	String afidValue = Base64.getEncoder().encodeToString(afid);
	//String afidValue = new String(afid.toString());
	//String result = URLConnectionReader.getText(url+afidValue);
	long a = System.currentTimeMillis();
//FaceMatchResultDTO dto = ByteArraySender.send(url[0], afid);
//long b = System.currentTimeMillis();	
//System.out.println("duration = "+(b-a));
	
	
	Set<Callable<FaceMatchResultDTO>> callables =getTaskList(afid);
	List<Future<FaceMatchResultDTO>> futures = executorService.invokeAll(callables);
	//FaceMatchResultDTO faceMatchResultDTO = WebcamViewerUtil.compare(sdk, query, afids);
	FaceMatchResultDTO faceMatchResultDTO = sort(futures); 
	
	long c = System.currentTimeMillis();
	System.out.println(faceMatchResultDTO);
	System.out.println("duration = "+(c-a));
	}

public static void prepareURLList(){
	url[0] = "http://localhost:8010/upload";
	url[1] = "http://localhost:9989/upload";
	url[2] = "http://localhost:9030/upload";
	url[3] = "http://localhost:9040/upload";
	url[4] = "http://localhost:9050/upload";
}

	public static Set<Callable<FaceMatchResultDTO>> getTaskList(byte[] query){
		Set<Callable<FaceMatchResultDTO>> callables = new HashSet<Callable<FaceMatchResultDTO>>();
	    //int size= database.size()/parallelizm;
	    FaceID _sdk=null;
	    String engine="";
		for (int i = 0; i < (parallelizm); i++) {

			TestCallableTask task = new TestCallableTask(url[i],query);
			callables.add(task);
		}
		
		return  callables;
}
	
	
	public static FaceMatchResultDTO sort(List<Future<FaceMatchResultDTO>> futures) throws Exception{
		float a = 0;
		Future<FaceMatchResultDTO> resultFuture=null;
		
		for (Iterator iterator = futures.iterator(); iterator.hasNext();) {
			Future<FaceMatchResultDTO> future = (Future<FaceMatchResultDTO>) iterator.next();
			float b =future.get().getScore();
			if(b>=a){
				a=b;
				resultFuture = future;
			}
		}
		
		FaceMatchResultDTO dto = new FaceMatchResultDTO(resultFuture.get().getAfid(), resultFuture.get().getScore());
		return dto;
	}
	
	
	
}
