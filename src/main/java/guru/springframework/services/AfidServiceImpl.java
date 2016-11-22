package guru.springframework.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ayonix.AynxFace;
import ayonix.AynxImage;
import ayonix.FaceID;
import guru.springframework.concurrent.CallableTask;
import guru.springframework.concurrent.FaceMatchResultDTO;
import guru.springframework.concurrent.ResultDto;
import guru.springframework.domain.Afid;
import guru.springframework.repositories.AfidRepository;

@Service
public class AfidServiceImpl implements AfidService {
	final static Logger logger = Logger.getLogger(AfidServiceImpl.class);
	Vector<byte[]> afids = new Vector<byte[]>();

    FaceID sdk;
	int parallelizm=1;
    
	
	private AfidRepository afidRepository;
    
	@Autowired
    public void setAfidRepository(AfidRepository afidRepository) {
        this.afidRepository = afidRepository;
    }
    
    @PostConstruct
    public void init(){
    	sdk = new FaceID("C:\\Program Files (x86)\\Ayonix\\FaceID\\data\\engine2");
    	//byte[] afid1= afidRepository.getAfidList().get(0);
    	AynxImage aynxImage = sdk.LoadImage("C:\\Users\\ETR00529\\Desktop\\ramazan.png");
    	AynxFace[] faces=sdk.DetectFaces(aynxImage);
    	if(faces.length>0){
    		sdk.PreprocessFace(faces[0]);
    		byte[] afid1  = sdk.CreateAfid(faces[0]);
    		for (int i = 0; i < 1000000; i++) {
    			afids.add(afid1);
    		}
    	}
    	afids.addAll(afidRepository.getAfidList());
    	logger.info(afids.size()+" adet afid Yuklendi");
    }

	@Override
	public void deleteAll(Collection<Afid> list) {
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Afid afid = (Afid) iterator.next();
			afidRepository.delete(afid);
		}
		
	}

	@Override
	public List<Afid> getAll() {
		return (List<Afid>)afidRepository.findAll();
	}

	@Override
	public FaceMatchResultDTO search(byte[] query) {
		long a = System.currentTimeMillis();
		
		ExecutorService executorService = Executors.newFixedThreadPool(parallelizm);
		
		try {
			Set<Callable<ResultDto>> callables =getTaskList(afids,query);
			List<Future<ResultDto>> futures = executorService.invokeAll(callables);
			FaceMatchResultDTO faceMatchResultDTO = sort(futures);
			
			long b = System.currentTimeMillis();
			logger.info("face duration : "+(b-a));
			return faceMatchResultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			
		} 
		return null;
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

	@Override
	public List<byte[]> getAfidList() {
		
		return afidRepository.getAfidList();
	}



    
	
}
