package ua.stellar.seatingchart.utils;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ua.stellar.seatingchart.domain.GoodsType;
import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.OperationType;

public class JsonUtils {

	private static final String DATA_RESULT_TAG = "result";

	private static <T> Object[] getArrayData(Gson gson, String json, Class<T> type) throws Exception {
		Object[] array = null;
		
		if (Layout.class == type) {
			array = gson.fromJson(json, Layout[].class);
		} else
		if (LayoutComposition.class == type) {
			array = gson.fromJson(json, LayoutComposition[].class);
		} else
		if (GoodsType.class == type) {
			array = gson.fromJson(json, GoodsType[].class);
		} else
		if (Operation.class == type) {
			array = gson.fromJson(json, Operation[].class);
		}
		if (OperationType.class == type) {
			array = gson.fromJson(json, OperationType[].class);
		}

		return array;
	}
	
	private static <T> List<T> getDataFromJson(String json, Class<T> className) throws Exception {
		List<T> list = null;		
    	Gson gson = new Gson();
			
		Map<String, Object> map = gson.fromJson(json, Map.class);
		String innerJson = gson.toJson(map.get(DATA_RESULT_TAG));
		Object[] array = getArrayData(gson, innerJson, className);
		list = (List<T>) Arrays.asList(array);			
		
		return list;		
	}

	public static <T> T getResponce(String url, Class<T> className) throws Exception {
		String responseData = "";
		T res = null;

		Gson gson = new Gson();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();

		URI objUrl = new URI(url);
		request.setURI(objUrl);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);

		if (json != "") {
			Map<String, Object> map = gson.fromJson(json, Map.class);
			responseData = gson.toJson(map.get(DATA_RESULT_TAG));

			res = gson.fromJson(responseData, className);
		}

		return res;
	}
	
	public static <T> List<T> sendRequest(String url, Class<T> className) throws Exception {
    	HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
		
        URI objUrl = new URI(url);
        request.setURI(objUrl); 
        HttpResponse response = client.execute(request); 
        HttpEntity entity = response.getEntity();      
    	String _response = EntityUtils.toString(entity); 
    	
		return getDataFromJson(_response, className);		
	}

	public static JsonResponse getJsonResponse(String url) throws Exception {
		JsonResponse res = null;

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);

		Gson gson = new Gson();
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpGet request = new HttpGet();

		URI objUrl = new URI(url);
		request.setURI(objUrl);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);

		try {
			if (json != "") {
				res = gson.fromJson(json, JsonResponse.class);
			}
		} catch (Exception e) {
			res = new JsonResponse(false, 0, "Ответ от сервера не получен");
		}

		if (res == null) {
			res = new JsonResponse(false, 0, "Ответ от сервера не получен");
		}

		return res;
	}

}
