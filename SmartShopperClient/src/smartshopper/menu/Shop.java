package smartshopper.menu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import smartshopper.library.DeleteItem;
import smartshopper.library.EmptyBasket;
import smartshopper.library.StoreBasket;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Shop extends Activity {
	private Button add;
	private TextView data;
	private NfcAdapter mNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "Shop";

	private ListView basketList;
	ArrayList<String> basket = new ArrayList<String>();
	ArrayList<String> deletedFromBasket = new ArrayList<String>();
	ArrayAdapter<String> arrayAdapter;
	public String BASKET_ITEMS;
	SharedPreferences sharedPrefs;
	Editor editor;
	String toChange;
	Button checkout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shop);
		add = (Button) findViewById(R.id.button1);
		data = (TextView) findViewById(R.id.textView1);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		loadBasket();

		
		// Prevents list bug of [] being displayed as the first entry
		if (basket.lastIndexOf("") == 0) {

			basket.remove(0);
		}
		System.out.println("Basket" + basket);

		// SCANS IN TAGS

		basketList = (ListView) findViewById(R.id.listView1);

		// This is the array adapter, it takes the context of the activity as a
		// first parameter, the type of list view as a second parameter and your
		// array as a third parameter.
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, basket);

		basketList.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (data.getText() == "") {
					System.out.println("Scan a tag");

				} else {
					System.out.println(data.getText());

					arrayAdapter.add(data.getText().toString());
					System.out.println(basket);
					new MyAsyncTask().execute(data.getText().toString());
					System.out.println("Item Added!");
					// Convert list to string
					String joined = TextUtils.join(", ", basket);
					System.out.println("Joined list  " + joined);
					saveList(joined);
				}

			}

		});
		basketList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				final String selectedFromList = (String) (basketList
						.getItemAtPosition(myItemInt));
				System.out.println("Item " + selectedFromList + " clicked");
				final int index = basket.indexOf(selectedFromList);

				System.out.println(index);
				// ADD DIALOG and if yes remove and call delete query
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Shop.this);

				alertDialogBuilder.setTitle("Remove from basket?");
				alertDialogBuilder.setMessage("Are you sure?");
				// set positive button: Yes message
				alertDialogBuilder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String lastDeleted=basket.get(index);
							    deletedFromBasket.add(lastDeleted);
								
							    lastDeleted=basket.get(index);
							    deletedFromBasket.add(lastDeleted);
							    basket.remove(index);
							    System.out.println("Deleted  "+deletedFromBasket);
								String joined = TextUtils.join(", ", basket);
								System.out.println("Joined list  " + joined);
								saveList(joined);
								// Data to be sent to query
								deleteFromDB(selectedFromList);

								arrayAdapter.notifyDataSetChanged();
							}
						});
				// set negative button: No message
				alertDialogBuilder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel the alert box and put a Toast to the
								// user

								dialog.cancel();
								Toast.makeText(getApplicationContext(),
										"Item not removed", Toast.LENGTH_LONG)
										.show();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.checkoutmenu, menu);

		MenuItem item = menu.findItem(R.id.action_checkout);

		// Return true to display menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_checkout:
			if(basket.size()>0){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Shop.this);
				alertDialogBuilder.setTitle("Checkout");
				alertDialogBuilder.setMessage("Do you want to checkout with this basket?");
				// set positive button: Yes message
				alertDialogBuilder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								checkout();
								
							}
						});
				// set negative button: No message
				alertDialogBuilder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel the alert box and put a Toast to the
								// user

								dialog.cancel();
								
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();

			
			}
			else
			{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Shop.this);
				System.out.println("Basket is empty");
				alertDialogBuilder.setTitle("Basket is empty");
				alertDialogBuilder.setMessage("Add items before checking out");
				// set positive button: Yes message
				alertDialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
							}
						});
				

				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();

				
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void checkout() {
		
		finish();
		passBasket();
		Intent go = new Intent(Shop.this, BeamActivity.class);
		startActivity(go);
	}

	public void deleteFromDB(String s) {
		System.out.println(s);
		new DeleteItem(this).execute(s);
	}

	public void saveList(String s) {
		StoreBasket.writeString(this, StoreBasket.BASKET, s);
	}

	public ArrayList<String> loadBasket() {

		toChange = (StoreBasket.readString(this, StoreBasket.BASKET, null));
		if (toChange == null) {
			System.out.println("EMPTY");
			return null;
		} else {
			System.out.println("To Change  " + toChange);
			toChange.length();
			System.out.println("Length of string " + toChange.length());
			if (toChange.length() == 2) {
				toChange = "";
			}
			Collections.addAll(basket, toChange.split("\\s*,\\s*"));

			if (basket.size() == 0) {

				System.out.println("basket is empty");
			}
			return basket;
		}

	}

	public void passBasket() {
		
		String pass = (StoreBasket.readString(this, StoreBasket.BASKET, null));
		StoreBasket.writeString(this, StoreBasket.BASKET_PASS, pass);
		//basket.clear();
		String toList = basket.toString();

		StoreBasket.writeString(this, StoreBasket.BASKET, toList);

	}

	@Override
	protected void onResume() {
		super.onResume();

		/**
		 * It's important, that the activity is in the foreground (resumed).
		 * Otherwise an IllegalStateException is thrown.
		 */
		if (basket.lastIndexOf("") == 0) {

			basket.remove(0);
		}
		System.out.println(basket);
		String toFix=basket.toString();
		toFix = toFix.replace("[", "");
		toFix =  toFix.replace("]", "");
		System.out.println("Invalid values gone "+toFix);
		basket.clear();
		
		basket.addAll(Arrays.asList(toFix.split("\\s*,\\s*")));
		
		saveList(toFix);
		System.out.println("Basket is"+basket);
		setupForegroundDispatch(this, mNfcAdapter);
		
		

	}

	@Override
	protected void onPause() {
		/**
		 * Call this before onPause, otherwise an IllegalArgumentException is
		 * thrown as well.
		 */
		stopForegroundDispatch(this, mNfcAdapter);

		super.onPause();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		/**
		 * This method gets called, when a new Intent gets associated with the
		 * current activity instance. Instead of creating a new activity,
		 * onNewIntent will be called. For more information have a look at the
		 * documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to
		 * the device.
		 */
		handleIntent(intent);
	}

	/**
	 * @param activity
	 *            The corresponding {@link Activity} requesting the foreground
	 *            dispatch.
	 * @param adapter
	 *            The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void setupForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(),
				activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(
				activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][] {};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters,
				techList);
	}

	
	public static void stopForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);

			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// In case we would still use the Type of NFC Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}

	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
						&& Arrays.equals(ndefRecord.getType(),
								NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record)
				throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at
			 * 3.2.1
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
					: "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength,
			// "US-ASCII");
			// e.g. "en"

			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				data.setText(result);

			}
		}

	}

	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected Double doInBackground(String... params) {

			postData(params[0]);
			return null;
		}

		public void postData(String valueIWantToSend) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://homepages.cs.ncl.ac.uk/s.c.g.campbell/smartshopper/map/createItems.php");

			try {
				// Send users uid to server so map they will see is personalised
				// to them.

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				// Post Current Aisle
				nameValuePairs.add(new BasicNameValuePair("item",
						valueIWantToSend));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request and get response from the server
				HttpResponse response = httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}

	}

}