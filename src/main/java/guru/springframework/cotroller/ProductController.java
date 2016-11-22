package guru.springframework.cotroller;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import guru.springframework.concurrent.FaceMatchResultDTO;
import guru.springframework.domain.Afid;
import guru.springframework.services.AfidService;

@RestController
@MultipartConfig(fileSizeThreshold = 20971520)
public class ProductController {

   
	private AfidService afidService;

    @Autowired
    public void setProductService(AfidService productService) {
        this.afidService = productService;
    }
    
    
    @RequestMapping(value = "/upload")
    public FaceMatchResultDTO uploadFile(@RequestParam("uploadedFile") MultipartFile uploadedFileRef){
    	FaceMatchResultDTO dto=null;
    	try {
			byte[] aa  = uploadedFileRef.getBytes();
			System.out.println("size:"+aa.length);
			dto= afidService.search(aa);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return dto;
    }
	
	
    @RequestMapping("product/{afid}")
    public FaceMatchResultDTO showProduct(@PathVariable String afid){
    	byte[] encoded = Base64.getEncoder().encode(afid.getBytes());
    	FaceMatchResultDTO dto= afidService.search(encoded);
    	
    	return dto;
    }

    @RequestMapping("product/id2")
    public String showProduct2(){
        Afid afid = new Afid();
        afid.setId(1l);;
    	return "ramazan";
    }
   
}
