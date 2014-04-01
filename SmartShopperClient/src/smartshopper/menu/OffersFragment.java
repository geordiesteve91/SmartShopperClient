package smartshopper.menu;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OffersFragment extends Fragment {

	private WebView myWebView;
	String mapPage = "http://homepages.cs.ncl.ac.uk/s.c.g.campbell/smartshopper/offers/index.html";

	String myUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		View rootView = inflater.inflate(R.layout.offers, container, false);
		if (myUrl == null) {
			myUrl = mapPage;
		}

		myWebView = (WebView) rootView.findViewById(R.id.offersweb);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.loadUrl(myUrl);
		myWebView.reload();

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
