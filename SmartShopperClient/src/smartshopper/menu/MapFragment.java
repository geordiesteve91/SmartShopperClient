package smartshopper.menu;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class MapFragment extends Fragment {

	

	private WebView myWebView;
	final static String mapPage = "http://homepages.cs.ncl.ac.uk/s.c.g.campbell/smartshopper/map/map.php";
	
	//Shared preference for store map.
	String myUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		View rootView = inflater.inflate(R.layout.map, container, false);
		if (myUrl == null) {
			myUrl = mapPage;
		}

		myWebView = (WebView) rootView.findViewById(R.id.mywebview);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.loadUrl(myUrl);

		return rootView;
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			myUrl = url;
			view.loadUrl(url);
			return true;
		}

	}
}
