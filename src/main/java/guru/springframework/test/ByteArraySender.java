package guru.springframework.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import guru.springframework.concurrent.FaceMatchResultDTO;



public class ByteArraySender {

public static FaceMatchResultDTO send(String url,byte[] afid) throws ClientProtocolException, IOException{
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httpPost = new HttpPost(url);

	//FileBody uploadFilePart = new FileBody(uploadFile);
	ContentBody cd = new InputStreamBody(new ByteArrayInputStream(afid), "my-file.txt");
	MultipartEntity reqEntity = new MultipartEntity();
	reqEntity.addPart("uploadedFile", cd);
	httpPost.setEntity(reqEntity);

	HttpResponse response = httpclient.execute(httpPost);
	String result = EntityUtils.toString(response.getEntity());
	
	JSONObject obj=(JSONObject)JSONValue.parse(result);
	String afid2 = (String)obj.get("afid");
	Double score = (Double)obj.get("score");
	FaceMatchResultDTO dto = new FaceMatchResultDTO(afid2.getBytes(), score.floatValue());
	
	
//	BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//	StringBuilder total = new StringBuilder();
//
//	String line = null;
//
//	while ((line = r.readLine()) != null) {
//	   total.append(line);
//	}
//	r.close();
	return dto;
	
}
}
