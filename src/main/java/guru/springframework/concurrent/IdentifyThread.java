package guru.springframework.concurrent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import ayonix.FaceID;

public	class IdentifyThread extends Thread {
	
	final static Logger logger = Logger.getLogger(IdentifyThread.class);
		
		FaceID sdk;
		byte[] query;
		Vector<byte[]> afids = new Vector<byte[]>();
//		BufferedImage image;
//		String cameraNAme;
//
//		Map<byte[],Person> personMap = new HashMap<byte[],Person>();
//		ApplicationContext context;
		
		int parallelizm=4;
		ExecutorService executorService = Executors.newFixedThreadPool(parallelizm);
		
		
		public IdentifyThread(FaceID sdk, byte[] query, Set<byte[]> afids) {
			super();
			this.sdk = sdk;
			this.query = query;
//			this.afids.addAll(afids);
//			this.image = image;
//			this.cameraNAme = cameraNAme;
//			this.personMap = personMap;
//			this.context = context;
		}

		public void run(){
			try {
				long s = System.currentTimeMillis();
				
				Set<Callable<ResultDto>> callables =getTaskList(afids,query);
				List<Future<ResultDto>> futures = executorService.invokeAll(callables);
				//FaceMatchResultDTO faceMatchResultDTO = WebcamViewerUtil.compare(sdk, query, afids);
				FaceMatchResultDTO faceMatchResultDTO = sort(futures); 
				
				 long d = System.currentTimeMillis();
				 logger.info("thread duration="+(d-s));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		
		
		
		public FaceMatchResultDTO sort(List<Future<ResultDto>> futures) throws Exception{
			float a = 0;
			Future<ResultDto> resultFuture=null;
			
			for (Iterator iterator = futures.iterator(); iterator.hasNext();) {
				Future<ResultDto> future = (Future<ResultDto>) iterator.next();
				float b =future.get().getScore();
				if(b>=a){
					a=b;
					resultFuture = future;
				}
			}
			
			FaceMatchResultDTO dto = new FaceMatchResultDTO(resultFuture.get().getAfid(), resultFuture.get().getScore());
			return dto;
		}
		
		public Set<Callable<ResultDto>> getTaskList(Vector<byte[]> database,byte[] query){
			Set<Callable<ResultDto>> callables = new HashSet<Callable<ResultDto>>();
		    int size= database.size()/parallelizm;
		    FaceID _sdk=null;
		    String engine="";
			for (int i = 0; i < (parallelizm-1); i++) {

				CallableTask task = new CallableTask(sdk,"",query,database.subList(i*size, (i+1)*size));
				callables.add(task);
			}
			
			CallableTask task = new CallableTask(sdk,engine,query,database.subList((parallelizm-1)*size, database.size()));
			callables.add(task);
			
			return  callables;
	}
		

	}

