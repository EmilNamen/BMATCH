package co.bmatch;

import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateDataActivity extends Activity {


	private EditText firstName;
	private EditText lastName;
	private EditText job;
	private EditText company;

	private ParseUser currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatedata_main);

		try {
			currentUser = ParseUser.getCurrentUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

		firstName = (EditText) findViewById(R.id.EditTextUpdateFirstName);
		lastName = (EditText) findViewById(R.id.EditTextUpdateLastName);
		job = (EditText) findViewById(R.id.EditTextUpdateJob);
		company = (EditText) findViewById(R.id.EditTextUpdateCompany);


		getSavedData( );


		Button buttonRefresh = (Button) findViewById(R.id.buttonUpdateData);
		buttonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CharSequence text = "Updating...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText( getApplicationContext( ), text, duration );
				toast.show( );

				try {
					currentUser.put("career", job.getText().toString());
					currentUser.put("company", company.getText().toString());
					currentUser.put("lastName", lastName.getText().toString());
					currentUser.put("name", firstName.getText().toString());
					currentUser.save();
					
					CharSequence text2 = "Done !";
					int duration2 = Toast.LENGTH_SHORT;

					Toast toast2 = Toast.makeText( getApplicationContext( ), text2, duration2 );
					toast2.show( );
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});


	}

	private void getSavedData() {

		try {
			firstName.setText(currentUser.getString("name"));
			lastName.setText(currentUser.getString("lastName"));
			company.setText(currentUser.getString("company"));
			job.setText(currentUser.getString("career"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
