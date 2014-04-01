package smartshopper.menu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Base64;
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
	String signature = "SmartShopperSignature";
	String test;
	String terminalsig = "w4vxqqkpeEAKY+IdhRE44q9ZI+d8RBhWOZPMI38Xx84=";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push);

		try {
			test = SHA256(signature);
			// /System.out.println(test);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		passedList = (StoreBasket.readString(this, StoreBasket.BASKET_PASS,
				null));
		message = (TextView) findViewById(R.id.textView1);
		message.setText("Scan Terminal to checkout");
		System.out.println("PassedList is " + passedList);
		ArrayList<String> checkout = new ArrayList<String>();
		Collections.addAll(checkout, passedList.split("\\s*,\\s*"));
		System.out.println("Checkout" + checkout);
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

	public ArrayList<String> getCheckout() {
		return checkout;

	}

	private static final String MIME_TYPE = "application/terminal.smartshopper";
	private static final String PACKAGE_NAME = "terminal.smartshopper";

	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		System.out.println("Test is" + test);

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		@SuppressWarnings("rawtypes")
		HashMap user = new HashMap();
		user = db.getUserDetails();
		String uid = user.get("email").toString();
		String text = uid + "," + numItems + "," + passedList + "///" + test;
		System.out.println("Text is" + text);
		// recieved.setText(uid);
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				NfcUtils.createRecord(MIME_TYPE, text.getBytes()),
				NdefRecord.createApplicationRecord(PACKAGE_NAME) });
		return msg;
	}

	public static String SHA256(String text) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(text.getBytes());
		byte[] digest = md.digest();

		return Base64.encodeToString(digest, Base64.DEFAULT);
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
				// emptyBasket();

				break;
			}
		}
	};

	public void emptyBasket() {
		String toEmpty = "true";
		new EmptyBasket(this).execute(toEmpty);
		// Refer to string that baskets load from
		//
		String empty = "";
		StoreBasket.writeString(this, StoreBasket.BASKET, empty);
		String check = (StoreBasket.readString(this, StoreBasket.BASKET, null));
		System.out.println("Check is" + check);

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

	@Override
	public void onBackPressed() {
		Intent go = new Intent(BeamActivity.this, Shop.class);
		startActivity(go);
	}

	void processIntent(Intent intent) {
		message.setText("");
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String payload = new String(msg.getRecords()[0].getPayload());
		

		System.out.println("Payload" + payload);
		if (payload.contains(terminalsig)) {
			System.out.println("Valid signature");
			recieved.setText("Checkout Successful");
			Thread timer = new Thread() {
				public void run() {
					try {
						sleep(5000);

					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.out.println("Time up");
						try {
							if (recieved.getText() != null) {

								sleep(8000);
								emptyBasket();
								Intent main = new Intent(BeamActivity.this,
										Main.class);
								startActivity(main);
							}

						} catch (InterruptedException e) {

							e.printStackTrace();
						}

					}
				}
			};
			timer.start();

		}
		else
		{
			//Display error
		}
	}

}
