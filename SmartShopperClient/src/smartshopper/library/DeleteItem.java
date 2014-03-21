package smartshopper.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteItem extends AsyncTask<String, Integer, Double> {
private Context context;
	
	public DeleteItem(Context context){
		this.context = context;
	}

	@Override
	protected Double doInBackground(String... params) {

		postData(params[0]);
		return null;
	}

	public void postData(String valueIWantToSend) {
		System.out.println("Value recieved is "+valueIWantToSend);
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://homepages.cs.ncl.ac.uk/s.c.g.campbell/smartshopper/map/deleteItem.php");
	
		try {
			

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			
			nameValuePairs.add(new BasicNameValuePair("toDelete",
					valueIWantToSend));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			 //Execute HTTP Post Request and get response from the server
			HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

}
