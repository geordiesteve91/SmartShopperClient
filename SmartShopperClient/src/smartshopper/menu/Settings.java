package smartshopper.menu;

import smartshopper.library.UserFunctions;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends Activity {
	Button btnLogout;
	Button changepas;
	Button shop;
	Button push;
	public static CharSequence pass;
	final CharSequence KEY_EMAIL = "EMAIL";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		changepas = (Button) findViewById(R.id.btchangepass);
		btnLogout = (Button) findViewById(R.id.logout);

		/**
		 * Change Password Activity Started
		 **/
		changepas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				Intent chgpass = new Intent(getApplicationContext(),
						ChangePassword.class);

				startActivity(chgpass);
			}

		});

		/**
		 * Logout from the User Panel which clears the data in Sqlite database
		 **/
		btnLogout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				UserFunctions logout = new UserFunctions();
				logout.logoutUser(getApplicationContext());
				Intent login = new Intent(getApplicationContext(), Login.class);
				login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(login);
				finish();
			}
		});

	}

}
