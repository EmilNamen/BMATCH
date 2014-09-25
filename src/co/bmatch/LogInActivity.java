package co.bmatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * MainActivity se realiza el registro de los datos
 * @author emilcamilonamenleon
 *
 */
public class LogInActivity extends Activity{


	private EditText login;
	private EditText password;


	private String chat_user_id;

	private ProgressDialog dialog;

	private String data;


	/**
	 * Project number
	 */
	String SENDER_ID = "163849948052"; 

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "BMATCH";


	private static LogInActivity instancia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_main);
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		login = (EditText) findViewById(R.id.EditTextlogin);
		password = (EditText) findViewById(R.id.EditTextPassword);
		if(login.getText().toString().isEmpty() && password.getText().toString().isEmpty() )
		{
			try{
				read();
			}
			catch(Exception e)
			{
				Log.d("Reading mydata", "error: "+e.getMessage());
			}
		}

		Button login = (Button) findViewById(R.id.buttonLogIn);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show( );
				checkUserData();
				save( );
				buttonLogIn( );
			}
		});

		Button signUp = (Button) findViewById(R.id.buttonSignUp);
		SpannableString content = new SpannableString("Registrarme en BMATCH");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		signUp.setText(content);
		signUp.setTextColor(Color.DKGRAY);
		signUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userSignUp( );
			}
		});

		Button forgetPassword = (Button) findViewById(R.id.buttonForgetPassword);
		SpannableString contentForgetPassword = new SpannableString("Olvidé mi contraseña");
		contentForgetPassword.setSpan(new UnderlineSpan(), 0, contentForgetPassword.length(), 0);
		forgetPassword.setText(contentForgetPassword);
		forgetPassword.setTextColor(Color.DKGRAY);
		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userForgetPassword( );
			}
		});



	}

	@SuppressLint("WorldWriteableFiles")
	@SuppressWarnings("deprecation")
	public void save( ){
		data = 	login.getText().toString()+"-"+
				password.getText().toString();
		try {
			FileOutputStream fOut = openFileOutput("mydata",MODE_WORLD_WRITEABLE);
			fOut.write(data.getBytes());
			fOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read( ){
		try{
			FileInputStream fin = openFileInput("mydata");
			int c;
			String temp="";
			String datas[] = null;
			while( (c = fin.read()) != -1){
				temp = temp + Character.toString((char)c);
				datas = temp.split("-");
			}
			login.setText(datas[0]);
			password.setText(datas[1]);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getUserLogin()
	{
		return login.getText().toString();
	}
	public String getUserPassword()
	{
		return password.getText().toString();
	}

	/**
	 * Metodo que revisa si los datos ingresados son validos para el registro
	 * @return True si son validos, False de lo contrario
	 */

	private boolean checkUserData( )
	{

		boolean ok = false;
		//Reviso que sean entradas de texto validas

		String loginString = login.getText().toString();
		String passwordString = password.getText().toString();

		if(loginString.isEmpty() == true || loginString.contains("@") != true || loginString.contains(".") != true)
		{
			login.setError("Login no valido");
		}
		else if(passwordString.isEmpty() == true)
		{
			password.setError("Clave no valida");
		}	
		else
		{
			ok = true;
		}
		return ok;
	}

	/**
	 * Metodo que realiza el registro con Parse.com
	 */

	public void buttonLogIn(  )
	{
		// Se realiza el login a BMATCH

		if(checkUserData() == false)
		{
			Log.d("User Data", "Error");
		}
		else
		{
			try {
				//Intenta hacer Login( )
				userLogIn();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void openMainBar( ){
		
		dialog = ProgressDialog.show(this, "BMATCH", "Validating Info...",true);
		dialog.setIcon(R.drawable.bmatchicon_alert);
		new Thread(new Runnable() {	
			@Override
			public void run()
			{
				Intent intentList = new Intent( getApplicationContext(), MainBar.class );
				startActivity( intentList );
				dialog.dismiss();
			}
		}).start();
	}


	public String getMasterId( )
	{
		return chat_user_id;
	}

	/**
	 * Metodo que realiza el log in es decir el registro si ya esta en la base de datos
	 */	
	@SuppressWarnings("deprecation")
	private void userLogIn( )
	{ 
		try {
			ParseUser.logInInBackground(getUserLogin(), getUserPassword(), new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {
						// Enter in BMATCH
						openMainBar();
					} else {
						// Signup failed. Look at the ParseException to see what happened.
						AlertDialog dialogAlert = new AlertDialog.Builder(LogInActivity.this).create();
						dialogAlert.setMessage("Error en los datos ingresados");
						dialogAlert.setButton("Aceptar",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton){
								dialog.cancel( );
							}
						});

						dialogAlert.setButton2("Registrarme",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton){
								userSignUp( );
							}
						});
						dialogAlert.show( );
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Metodo que realiza el sign up es decir el registro no esta en la base de datos
	 */
	private void userSignUp( )
	{
		Intent intentSignUp = new Intent( getApplicationContext(), SignUpActivity.class );
		startActivity( intentSignUp );
	}

	/**
	 * Metodo que manda email si el usuario olvido su contraseña
	 */
	private void userForgetPassword() {

		AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
		builder.setTitle("RESETEAR CONTRASEÑA");
		builder.setIcon(R.drawable.alert_icon);
		builder.setMessage("Ingrese su usuario: ");

		// Use an EditText view to get user input.
		final EditText input = new EditText(this);
		input.setId(0);
		builder.setView(input);

		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				if(!input.getText().toString().isEmpty()){
					try {
						CharSequence text = "Please wait...";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(getApplicationContext(), text, duration);
						toast.show();
						ParseUser.requestPasswordReset(input.getText().toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}					
				}
			}
		});

		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		builder.create();
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static LogInActivity darInstancia( )
	{
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

