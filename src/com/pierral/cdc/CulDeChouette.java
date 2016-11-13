package com.pierral.cdc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CulDeChouette extends ListActivity {
	
	public final static String PLAYERS = "players_list" ;
	
	private ArrayList<CDCPlayer> m_listPlayers = new ArrayList<CDCPlayer>();
	private ArrayAdapter<CDCPlayer> m_adapter;
	
	private String m_contextFileName;
	
	public static Typeface typeFace = null;
	

	static public boolean AUTO_SAVE = true;
	static public boolean COMPLETE = true;
	
	private SharedPreferences sharedPrefs;
	
	private class CDCPlayerAdapter extends ArrayAdapter<CDCPlayer> {

		public CDCPlayerAdapter(Context context, int resource, int textViewResourceId,
				List<CDCPlayer> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@ Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View v = convertView;

			LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
			
			if (v==null) {
				v = li.inflate(R.layout.player_row, null);
				
			}
			
			final CDCPlayer p = getItem(position);
			
			TextView n = (TextView) v.findViewById(R.id.playerRowName);
			n.setTypeface(typeFace);
			if (p != null) {
				n.setText(p.getName());
			}
			
			ImageView i = (ImageView) v.findViewById(R.id.delPlayer);
			i.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					displayDeletePlayerDialog(position);
				}
			});
			
			return v;
		}
		
	}

    @TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cul_de_chouette);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	ActionBar actionBar = getActionBar();
        	actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        	actionBar.setTitle("");
        }
        
        // set fonts
        typeFace = Typeface.createFromAsset(getAssets(), "Cardinal.ttf");
        Button bt = (Button) findViewById(R.id.newPlayerBtn);
        bt.setTypeface(typeFace);
        bt = (Button) findViewById(R.id.noticeBtn);
        bt.setTypeface(typeFace);
        bt = (Button) findViewById(R.id.startGameBtn);
        bt.setTypeface(typeFace);
        

        m_contextFileName = getString(R.string.ContextFileName);
        
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		setPreferences();		
		
		if (AUTO_SAVE) {
			restoreContext(m_contextFileName);
		}

        m_adapter = new CDCPlayerAdapter (this, android.R.layout.simple_list_item_1, R.id.playerRowName, m_listPlayers);
        setListAdapter(m_adapter);
    }
    
	
	public void onPause() {
		super.onPause();
		if (AUTO_SAVE) {
			saveContext(m_contextFileName);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cul_de_chouete, menu);
        return true;
    }
    
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_settings:
	    	launchSettings();
	        return true;
	        /*
	    case R.id.menu_clear:
	    	clear();
	    	return true;
	    	*/
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	

    public void setPreferences() {
		AUTO_SAVE = sharedPrefs.getBoolean("AutoSave", true);	
		COMPLETE = sharedPrefs.getBoolean("Complete", true);
	}
	
	private void clear() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setMessage(getString(R.string.delAllPlayerSentence));
		
		alert.setTitle(getString(R.string.delPlayerTitle));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			m_listPlayers.clear();
			m_adapter.notifyDataSetChanged();
		}
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), null);

		alert.show();
	}
    
    public void playGame(View v) {
    	if (m_listPlayers.size() > 1) {
    		Intent intent =  new Intent(this, CDCGame.class);
    		intent.putExtra(CulDeChouette.PLAYERS, m_listPlayers);
    		startActivity(intent);
    	}
    	else {
    		Toast.makeText(getApplicationContext(), getString(R.string.ErrorNoPlayers), Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void launchSettings() {
    	Intent t = new Intent(getApplicationContext(), SettingsActivity.class);
		this.startActivityForResult(t, 2);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 2) {
    		setPreferences();
    	}
    }
    
    public void addPlayer(View v) {
    	displayAddPlayerDialog();
    	m_adapter.notifyDataSetChanged();
    }
    
    public void showNotice(View v) {
    	
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE) ;
    	View layout = inflater.inflate(R.layout.notice,(ViewGroup ) findViewById(R.id.notice_layout));
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(getString(R.string.NoticeTitle));
		alert.setView(layout);

		alert.setNegativeButton(getString(R.string.NoticeOkLbl), null);

		alert.show();
    }
    
	private void displayAddPlayerDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setTitle(getString(R.string.addPlayerTitle));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  if (value.length() > 0) {
			  m_listPlayers.add(new CDCPlayer(value));
		  }
		  else {
			  Toast.makeText(getApplicationContext(), getString(R.string.ErrorEmptyName), Toast.LENGTH_SHORT).show();
		  }
		 }
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), null);

		alert.show();
	}

	private void displayDeletePlayerDialog(final int position) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		CDCPlayer p = m_adapter.getItem(position);
		if (p == null) {
			return;
		}
		// Set an EditText view to get user input 
		final TextView input = new TextView(this);
		input.setText(getString(R.string.delPlayerSentence) + " " + p.getName() + " ?");
		input.setPadding(5, 5, 5, 5);
		alert.setView(input);
		
		alert.setTitle(getString(R.string.delPlayerTitle));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  m_listPlayers.remove(position);
		  m_adapter.notifyDataSetChanged();
		}
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), null);

		alert.show();
	}
	
	private void saveContext(String filename) {
		try {
			FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fout);
			os.writeObject(m_listPlayers);
			os.close();
		} catch (FileNotFoundException exc) {
			Toast.makeText(this, "Error saving context...", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error opening Serialize object..", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void restoreContext(String filename) {
		try {
			FileInputStream fin = openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(fin);
			ArrayList<CDCPlayer> readObject = (ArrayList<CDCPlayer>) is.readObject();
			m_listPlayers = readObject;
			is.close();
		} catch (FileNotFoundException exc) {
			Toast.makeText(this, "Error restoring context...", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error restoring Serialize object..", Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			Toast.makeText(this, "Error restoring Serialize object..2", Toast.LENGTH_SHORT).show();
		}
	}
	
    
}
