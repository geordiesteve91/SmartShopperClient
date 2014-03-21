package smartshopper.library;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StoreBasket {

	public static final String PREF_NAME = "BASKET_DATA";

	public static final int MODE = Context.MODE_PRIVATE;

	

	public static final String BASKET = "BASKET";
	public static final String BASKET_PASS="PASSED_BASKET";

	

	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}
	public static void writeEmpty(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}

	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}
}
