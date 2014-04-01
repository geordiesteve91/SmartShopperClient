package smartshopper.menu;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Favourites extends Fragment {
	private ProgressDialog dialog;

	private WebView myWebView;
	final static String mapPage = "http://homepages.cs.ncl.ac.uk/s.c.g.campbell/smartshopper/fav/index.html";

	String myUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		View rootView = inflater.inflate(R.layout.favourites, container, false);
		if (myUrl == null) {
			myUrl = mapPage;
		}

		myWebView = (WebView) rootView.findViewById(R.id.favsweb);
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
