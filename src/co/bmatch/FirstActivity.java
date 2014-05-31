package co.bmatch;

import java.util.List;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

		//PARSE
		Parse.initialize(getApplicationContext(), "STb6FUhezrSjIdBuTdt7RXeKIRZ3uFZv2CXBKqit", "xXKovwAw1g0rkiThTXkOgPOz8OzI9CnhmqgNwWE6");
		
		textEventCode = (EditText) findViewById(R.id.editCodeText);
		textEventCode.setGravity(Gravity.CENTER);

		try{
			Button buttonCode = (Button) findViewById(R.id.buttonEnter);
			buttonCode.setOnClickListener(new OnClickListener() {			

				public void onClick(View v) {

					if(textEventCode.getText().toString().isEmpty())
					{
						//QR-READER
						IntentIntegrator scanIntegrator = new IntentIntegrator(FirstActivity.this);
						scanIntegrator.initiateScan();
//						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//						startActivityForResult(intent, 0);
						
//						Intent intent = new Intent("com.google.zxing.client.android.CaptureActivity");
//						intent.setAction("com.google.zxing.client.android.SCAN");
//						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//						startActivityForResult(intent, 0);
					}		


					// Create query for objects of type "Post"
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Evento");

					// Restrict to cases where the author is the current user.
					// Note that you should pass in a ParseUser and not the
					// String representation of that user
					query.whereEqualTo("objectId", getEventCode());
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
								mainView();
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

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  
		if (scanResult !=null) {
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
	}



	public String getEventCode(){
		return textEventCode.getText().toString();
	}


	public void mainView() {    			
		Intent intentMain = new Intent( getApplicationContext(), LogInActivity.class );
		startActivity( intentMain );
	}

	public static FirstActivity darInstancia(){
		return instancia;
	}
	
	@Override
	protected void onPause() {
        super.onPause();
    }
}
