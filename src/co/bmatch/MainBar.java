package co.bmatch;


import android.os.Bundle;
import android.content.Intent;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.app.TabActivity;
import android.widget.TabHost.OnTabChangeListener;
	
@SuppressWarnings("deprecation")
public class MainBar extends TabActivity implements OnTabChangeListener{
	
	TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_bar);

		// Get TabHost Reference
		tabHost = (TabHost)findViewById(android.R.id.tabhost);

		// Set TabChangeListener called when tab changed
		//tabHost.setOnTabChangedListener(this);
		
		TabSpec tab1spec = tabHost.newTabSpec("First Tab");
        TabSpec tab2spec = tabHost.newTabSpec("Second Tab");
        TabSpec tab3spec = tabHost.newTabSpec("Third tab");
        
		Intent intent;
		
		
		/************* TAB1 ************/
		// Create  Intents to launch an Activity for the tab (to be reused)
		intent = new Intent(getApplicationContext(), UpdateDataActivity.class);
		tab1spec.setIndicator("");
		tab1spec.setContent(intent);

		//Add intent to tab
		tabHost.addTab(tab1spec);

		/************* TAB2 ************/
		intent = new Intent(getApplicationContext(), FirstActivity.class);
		
		tab2spec.setIndicator("");
		tab2spec.setContent(intent);

		//Add intent to tab
		tabHost.addTab(tab2spec);
		
		
		/************* TAB3 ************/
		intent = new Intent(getApplicationContext(), EventsHistoryActivity.class);
        
        tab3spec.setIndicator("");
		tab3spec.setContent(intent);

		//Add intent to tab
		tabHost.addTab(tab3spec);
        
        
        tabHost.setCurrentTab(1);
//        // Set drawable images to tab
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.codigo_tab);
        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.historial_tab);
           
        // Set Tab1 as Default tab and change image   
        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.info_tab);


	}

	@Override
	public void onTabChanged(String tabId) {

		/************ Called when tab changed *************/

		//********* Check current selected tab and change according images *******/

//		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
//		{
//			if(i==0)
//				tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bmatchicon);
//			else if(i==1)
//				tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bmatchicon2);
//		}
//
//
//		if(tabHost.getCurrentTab()==0)
//			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.bmatchicon2);
//		else if(tabHost.getCurrentTab()==1)
//			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.bmatchicon);

	}
	
	@Override
	protected void onPause( ) {
		super.onPause();
	}


	@Override
	protected void onStop( ) {
		super.onStop();

	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
