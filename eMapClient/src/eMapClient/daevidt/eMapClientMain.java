package eMapClient.daevidt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class eMapClientMain extends Activity {
    /** Called when the activity is first created. */
	
	private int serverPort = R.integer.serverPort;
	private String serverAddress = getString(R.string.serverAddress);
	private Socket serverSocket = null;
	private DataOutputStream dataSend = null;
	private DataInputStream dataReceive = null;
	
	private static final int SERVER_ADDRESS_DIALOG = 1;
	TextView console;
	EditText commandLine;
	
	public String getServerAddress(){
		return serverAddress;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set layout
        setContentView(R.layout.main);

        //initialize
        console = (TextView) findViewById(R.id.textView2);
        console.setMovementMethod(new ScrollingMovementMethod());
        
        commandLine = (EditText) findViewById(R.id.editText1);   
        //CommandLine editorEvent
        commandLine.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				//enter
				switch (actionId)
				{
				case 0:
					EditText input = (EditText) v;
					String s = input.getText().toString();
					if(s.equals("")){}
					else
					{
						console.append("\n>> " + s);
						if(serverSocket != null)
						{
							sendText(s);
							console.append("\n<< " + receiveText());	
						}
			    		//egyébként csak konzolba
			    		else
			    		{
			    			if (s.equals("server"))
			    			{
			    				console.append("\nserver: " + serverAddress + ":" + serverPort);
			    			}
							console.append(" (offline)");
						}
			    		
						input.setText("");
					}
					return true;
				default:
					return false;
				}
			}
		});
        
     }
    protected void sendText(String textToSend)
    {
    	try{
    		byte[] utf8bytes = textToSend.getBytes("UTF8");
    		dataSend.write(utf8bytes);
    	}
    	catch(UnknownHostException e)
		{
			console.append("\n[Unknown host]");
			e.printStackTrace();
		}
		catch(IOException e)
		{
			console.append("\n[IO Error]");
			e.printStackTrace();
		}
    }
    
    protected String receiveText()
    {
    	try{
    		byte[] recvBytes = new byte[1024];
    		dataReceive.read(recvBytes);
    		
    		String s = new String(recvBytes, "UTF-8");
    		s = s.substring(0, s.indexOf("\0"));
    		return s;

    	}
    	catch(UnknownHostException e)
    	{
    		console.append("\n[Unknown host]");
    		e.printStackTrace();
    	}
    	catch(IOException e)
    	{
    		console.append("\n[IO Error]");
    		e.printStackTrace();
    	}
		return "";
    	
    }
    
    @Override
    protected void onDestroy()
    {
    	if (serverSocket != null){
    		   try {
    		    serverSocket.close();
    		   } catch (IOException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		   }
    		  }

    		  if (dataSend != null){
    		   try {
    		    dataSend.close();
    		   } catch (IOException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		   }
    		  }

    		  if (dataReceive != null){
    		   try {
    		    dataReceive.close();
    		   } catch (IOException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		   }
    		  }
    		  super.onDestroy();
    }
    
    @Override	//Fõmenü létrehozása
    public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
    	return true;
    
    }
    
    @Override  //Menüelemek hatása
    public boolean onOptionsItemSelected(MenuItem item){
    	//Kiválasztott elem kezelése
    	switch(item.getItemId())
    	{
    	case R.id.exit:
    		finish();
    		return true;
    		
    	case R.id.server_addr:
    		//Szerver cimét bekérõ dialog
    		showDialog(SERVER_ADDRESS_DIALOG);
    		return true;
    	case R.id.connect:
    		//Csatlakozás
    		try {
				serverSocket = new Socket(serverAddress, serverPort);
				dataSend = new DataOutputStream(serverSocket.getOutputStream());
				dataReceive = new DataInputStream(serverSocket.getInputStream());
				console.append("\nconnected to " + serverAddress + ":" + serverPort);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
    		catch(Exception e)
    		{
    			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    		}
    		return true;
    	case R.id.display_map:
    		Intent displayMapIntent = new Intent(eMapClientMain.this, MapActivity.class);
    		eMapClientMain.this.startActivity(displayMapIntent);
    		return true;
    		
			
    	default: return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id)
    	{
    	case SERVER_ADDRESS_DIALOG:
    		
    	 LayoutInflater factory = LayoutInflater.from(this);
         final View textEntryView = factory.inflate(R.layout.serveraddr_dialog, null);
         
         // initialize form with current values
         EditText s_addr = (EditText)textEntryView.findViewById(R.id.edit_server_address);
    	 EditText s_port = (EditText)textEntryView.findViewById(R.id.edit_server_port);
         s_addr.setText(serverAddress);
    	 s_port.setText(Integer.toString(serverPort));
         
    	 //create dialog
         return new AlertDialog.Builder(eMapClientMain.this)
             .setIcon(R.drawable.icon)
             .setTitle(R.string.server_addr)
             .setView(textEntryView)
             .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 //Accepted changes, set them
                	 if (whichButton == DialogInterface.BUTTON_POSITIVE)
                	 {
	                	 EditText s_addr = (EditText)textEntryView.findViewById(R.id.edit_server_address);
	                	 EditText s_port = (EditText)textEntryView.findViewById(R.id.edit_server_port);
	                	 serverAddress = s_addr.getText().toString();
	                	 serverPort = Integer.parseInt(s_port.getText().toString());
                	 }
                 }
             })
             .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                     /* User clicked cancel so do some stuff */
                 }
             })
             .create();
    	}
    	
		return null;
    }
    
 
}