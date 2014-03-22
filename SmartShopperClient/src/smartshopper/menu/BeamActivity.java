package smartshopper.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import smartshopper.library.DatabaseHandler;
import smartshopper.library.EmptyBasket;
import smartshopper.library.StoreBasket;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class BeamActivity extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	NfcAdapter mNfcAdapter;
	TextView recieved;
	String passedList;
	ArrayList<String> checkout;
	int numItems;
	TextView message;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push);
		passedList = (StoreBasket.readString(this, StoreBasket.BASKET_PASS,
				null));
		message = (TextView) findViewById(R.id.textView1);
		System.out.println("PassedList is " + passedList);
		ArrayList<String> checkout= new ArrayList<String>();
		Collections.addAll(checkout, passedList.split("\\s*,\\s*"));
		System.out.println("Checkout"+checkout);
		System.out.println("Passed Length " + checkout.size());
		numItems = checkout.size();

		recieved = (TextView) findViewById(R.id.HelloTag);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "Sorry, NFC is not available on this device",
					Toast.LENGTH_SHORT).show();
		} else {
			// Register callback to set NDEF message
			mNfcAdapter.setNdefPushMessageCallback(this, this);
			// Register callback to listen for message-sent success
			mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		}

	}
	public ArrayList<String> getCheckout()
	{
		return checkout;
		
	}

	private static final String MIME_TYPE = "application/terminal.smartshopper";
	private static final String PACKAGE_NAME = "terminal.smartshopper";

	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		System.out.println("Does it work");
		
		
		System.out.println("Items # in basket is "+numItems);
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		@SuppressWarnings("rawtypes")
		HashMap user = new HashMap();
		user = db.getUserDetails();
		String uid = user.get("email").toString();
		String text = uid +","+numItems+","+passedList;
		System.out.println("Text is"+text);
		// recieved.setText(uid);
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				NfcUtils.createRecord(MIME_TYPE, text.getBytes()),
				NdefRecord.createApplicationRecord(PACKAGE_NAME) });
		return msg;
	}

	private static final int MESSAGE_SENT = 1;

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(getApplicationContext(), "Basket sent!",
						Toast.LENGTH_LONG).show();
				emptyBasket();
//				Intent wait = new Intent(BeamActivity.this, Finished.class);
//				startActivity(wait);
				break;
			}
		}
	};
	public void emptyBasket()
	{
		String toEmpty = "true";
		new EmptyBasket(this).execute(toEmpty);
		
	}

	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	/**
	 * Parses the NDEF Message from the intent and toast to the user
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// in this context, only one message was sent over beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String payload = new String(msg.getRecords()[0].getPayload());
		recieved.setText(payload);
		Toast.makeText(getApplicationContext(),
				"Message received over beam: " + payload, Toast.LENGTH_LONG)
				.show();
	}
	
}
