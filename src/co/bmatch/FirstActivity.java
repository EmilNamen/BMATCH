package co.bmatch;

import java.util.List;

import com.google.zxing.client.android.CaptureActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstActivity extends Activity{

	private EditText textEventCode;	

	private static FirstActivity instancia;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_main);
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		textEventCode = (EditText) findViewById(R.id.editCodeText);
		textEventCode.setGravity(Gravity.CENTER);

		try{
			Button buttonCode = (Button) findViewById(R.id.buttonEscanear);
			buttonCode.setOnClickListener(new OnClickListener() {			

				public void onClick(View v) {

					if(textEventCode.getText().toString().isEmpty())
					{
						//QR-READER


						Intent intent = new Intent(FirstActivity.this, CaptureActivity.class); 
						intent.setAction("com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						startActivityForResult(intent, 0);
					}		


					// Create query for objects of type "Post"
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");

					// Restrict to cases where the author is the current user.
					// Note that you should pass in a ParseUser and not the
					// String representation of that user
					query.whereEqualTo("objectId", getEventCode( ));
					try {
						List<ParseObject> events = query.find();
						if(events.isEmpty() && !textEventCode.getText().toString().isEmpty())
						{
							textEventCode.setError("Codigo no valido");
							Log.d("BMATCH", "Error: No se encontro el evento");
						}
						else
						{
							if(!textEventCode.getText().toString().isEmpty())
							{
								registerEventUser( );

							}
						}
					} catch (ParseException e1) {
						Log.d("ParseException", "Error: Problemas con el evento escrito");
						textEventCode.setError("Codigo no valido");
						e1.printStackTrace();
					}
				}


			});
		}
		catch (Exception e) {
			Log.d("QR-Error", "Error: Problemas con el qr" + e);
		}



		Button enterEvent = (Button) findViewById(R.id.buttonIngresarEvento);
		enterEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(textEventCode.getText().toString().isEmpty())
				{
					textEventCode.setHint("Código no valido");
				}
				else
				{
					try {
						// Create query for objects of type "Post"
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");

						// Restrict to cases where the author is the current user.
						// Note that you should pass in a ParseUser and not the
						// String representation of that user
						query.whereEqualTo("objectId", getEventCode());
						List<ParseObject> events = query.find();
						if(events.isEmpty() && !textEventCode.getText().toString().isEmpty())
						{
							textEventCode.setError("Codigo no valido");
							Log.d("BMATCH", "Error: No se encontro el evento");
						}
						else
						{
							if(!textEventCode.getText().toString().isEmpty())
							{
								registerEventUser( );
							}
						}
					} catch (ParseException e1) {
						Log.d("ParseException", "Error: Problemas con el evento escrito");
						textEventCode.setError("Codigo no valido");
						e1.printStackTrace();
					}
				}

			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (resultCode == RESULT_OK) {
			String contents = intent.getStringExtra("SCAN_RESULT");
			textEventCode.setText(contents);

			// Handle successful scan
		} else if (resultCode == RESULT_CANCELED) {
			// Handle cancel
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	private void registerEventUser() {

		try {
			ParseUser currentUser = ParseUser.getCurrentUser();

			// Reviso si existe el chatUser
			ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("EventUser");
			queryUser.whereEqualTo("user", currentUser);
			List<ParseObject> usersChat = queryUser.find();
			if(usersChat.isEmpty())
			{
				ParseObject eventUser = new ParseObject("EventUser");
				eventUser.put("user", currentUser);
				eventUser.put("event", ParseObject.createWithoutData("Event", getEventCode( )));
				eventUser.save();
				peopleView();
			}
			else
			{
				// Show the list of people in event
				peopleView();
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getEventCode(){
		return textEventCode.getText().toString();
	}


	public void peopleView( ) {    			
		Intent intentMain = new Intent( FirstActivity.this, TabBar.class );
		intentMain.putExtra("EVENT_ID", getEventCode( ));
		System.out.println("Envio id: "+getEventCode());
		startActivity( intentMain );
	}

	public static FirstActivity darInstancia(){
		return instancia;
	}

	@Override
	protected void onPause( ) {
		super.onPause();
	}


	@Override
	protected void onStop( ) {
		super.onStop();

	}
}
