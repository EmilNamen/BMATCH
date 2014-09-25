 package co.bmatch;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
 * SignUpActivity activity that handles the signup of a new user into BMATCH
 * @author emilcamilonamenleon
 *
 */
public class SignUpActivity extends Activity{


	private EditText firstName;
	private EditText lastName;
	private EditText login;
	private EditText job;
	private EditText company;
	private EditText password;
	private EditText confirmPassword;
	
	private ProgressDialog dialog;



	private String chat_user_id;

	private ParseUser user;


	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "BMATCH";


	private static SignUpActivity instancia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_main);
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		firstName = (EditText) findViewById(R.id.EditTextfirstName);
		lastName = (EditText) findViewById(R.id.EditTextlastName);
		job = (EditText) findViewById(R.id.EditTextjob);
		company = (EditText) findViewById(R.id.EditTextCompany);
		login = (EditText) findViewById(R.id.EditTextemail);
		password = (EditText) findViewById(R.id.EditTextpassword);
		confirmPassword = (EditText) findViewById(R.id.EditTextconfirmPassword);

		Button register = (Button) findViewById(R.id.buttonRegister);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText( getApplicationContext( ), text, duration );
				toast.show( );
				checkUserData( );
				buttonRegister( );
			}
		});
	}

	public String getEventCode()
	{
		return FirstActivity.darInstancia().getEventCode();
	}	

	public String getUserFirstName()
	{
		return firstName.getText().toString();
	}
	public String getUserLastName()
	{
		return lastName.getText().toString();
	}
	public String getUserJob()
	{
		return job.getText().toString();
	}
	public String getUserLogin()
	{
		return login.getText().toString();
	}
	public String getUserPassword()
	{
		return password.getText().toString();
	}
	public String getUserConfirmPassword()
	{
		return confirmPassword.getText().toString();
	}

	public String getUserCompany()
	{
		return company.getText().toString();
	}

	/**
	 * Metodo que revisa si los datos ingresados son validos para el registro
	 * @return True si son validos, False de lo contrario
	 */

	private boolean checkUserData()
	{

		boolean ok = false;
		//Reviso que sean entradas de texto validas

		String firstNameString = firstName.getText().toString();
		String lastNameString = lastName.getText().toString();
		String jobString = job.getText().toString();
		String companyString = company.getText().toString();
		String emailString = login.getText().toString();
		String passwordString = password.getText().toString();
		String confirmPasswordString = confirmPassword.getText().toString();

		if(firstNameString.isEmpty() == true || firstNameString.length()>30)
		{
			firstName.setError("Nombre no valido");
		}
		else if(lastNameString.isEmpty() == true || lastNameString.length()>30)
		{
			lastName.setError("Apellido no valido");
		}
		else if(jobString.isEmpty() == true)
		{
			job.setError("Profesion no valida");
		}
		else if(companyString.isEmpty() == true)
		{
			company.setError("Empresa no valida");
		}	
		else if(emailString.isEmpty() == true || emailString.contains("@") != true || emailString.contains(".") != true)
		{
			login.setError("email login no valido");
		}
		else if(passwordString.isEmpty() == true)
		{
			password.setError("Clave no valida");
		}
		else if(confirmPasswordString.isEmpty() == true )
		{
			confirmPassword.setError("Clave no valida");
		}		 
		else if(confirmPasswordString.equals(passwordString)==false)
		{
			confirmPassword.setError("La clave no es la misma");
		}
		else
		{
			ok = true;
		}
		return ok;
	}

	public String getMasterId()
	{
		return chat_user_id;
	}

	/**
	 * Metodo que realiza el sign up es decir el registro no esta en la base de datos
	 */
	@SuppressWarnings("deprecation")
	private void buttonRegister()
	{
		if(checkUserData() == true)
		{
			user = new ParseUser( );
			user.setUsername( getUserLogin( ) );
			user.setPassword( getUserConfirmPassword( ) );
			user.put("email", getUserLogin());
			user.put("name", getUserFirstName());
			user.put("lastName", getUserLastName());
			user.put("career", getUserJob());
			user.put("company", getUserCompany());
			try {
				user.signUp( );
				userLogIn( );
			} catch (ParseException e) {
				e.printStackTrace();
				AlertDialog dialogAlert = new AlertDialog.Builder(SignUpActivity.this).create();
				dialogAlert.setMessage("Error en los datos ingresados");
				dialogAlert.setButton("Aceptar",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton){
						dialog.cancel( );
					}
				});
			}
		}
	}
	
	
	/**
	 * Metodo que realiza el log in es decir el registro si ya esta en la base de datos
	 */	
	private void userLogIn( )
	{ 
		try {
			ParseUser.logInInBackground(getUserLogin(), getUserPassword(), new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {
						// Enter in BMATCH
						openMainBar();
					} else {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void openMainBar( ){

		dialog = ProgressDialog.show(this, "BMATCH", "Validating Info...",true);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static SignUpActivity darInstancia()
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
