package com.pierral.cdc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CDCGame extends ListActivity {

	private ArrayList<CDCPlayer> m_listPlayers = new ArrayList<CDCPlayer>();
	private int[][] m_sirotage;
	private int m_curPlayerIndex;
	private TextView m_titleTV;
	private ArrayAdapter<CDCPlayer> m_adapter;
	
	public static Typeface tf = null;
	
	static final int CHOUETTE = 0;
	static final int VELUTE = 1;
	static final int CHOUETTE_VELUTE = 2;
	static final int SUITE = 3;
	static final int CUL_DE_CHOUETTE = 4;
	static final int BEVUE = 5;
	static final int GRELOTTINE = 6;
	
	static final int SPECIAL_CIVET = 1;
	static final int SPECIAL_GRELOTTINE = 2;

	static final int BET_CHOUETTE = 0;
	static final int BET_VELUTE = 1;
	static final int BET_CHOUETTE_VELUTE = 2;
	static final int BET_SUITE = 3;
	static final int BET_CUL_DE_CHOUETTE_SANS_SIROTAGE = 4;
	static final int BET_CUL_DE_CHOUETTE_AVEC_SIROTAGE = 5;
	
	static final int[] CHOUETTE_POINTS = { 1, 4, 9, 16, 25, 36 };
	static final int[] VELUTE_POINTS = { 2, 8, 18, 32, 50, 72 };
	static final int[] CUL_DE_CHOUETTE_POINTS = { 50, 60, 70, 80, 90, 100 };
	
	static final int DIALOG_SUITE = 1;
	static final int DIALOG_CHOUETTE_VELUTE = 2;
	static final int DIALOG_BEVUE = 3;
	static final int DIALOG_GRELOTTINE = 4;
	static final int DIALOG_CIVET = 5;
	
	private class CDCPlayerGameAdapter extends ArrayAdapter<CDCPlayer> {

		public CDCPlayerGameAdapter(Context context, int resource, int textViewResourceId,
				List<CDCPlayer> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@ Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View v = convertView;

			LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
			
			if (v==null) {
				v = li.inflate(R.layout.player_game_row, null);
			}
		
			final CDCPlayer p = getItem(position);
			
			if (p != null) {
				TextView n = (TextView) v.findViewById(R.id.playerGameRowName);
				n.setText(p.getName());
				n.setTypeface(tf);
				n = (TextView) v.findViewById(R.id.playerGamePoints);
				n.setText(p.getPoints() + (p.getPoints() > 1 || p.getPoints() < -1 ? getString(R.string.PointsLabelShort) : getString(R.string.PointLabelShort)));
				n = (TextView) v.findViewById(R.id.playerGameGrelotine);
				n.setText(" ");
				if (p.hasGrelottine()) {
					if (p.getGrelottinePoints() > 0) {
						n.setText("G:"+p.getGrelottinePoints()+"pts");
					}
					else {
						n.setText("G");
					}
				}
				n = (TextView) v.findViewById(R.id.playerGameCivet);
				n.setText(" ");
				if (p.hasCivet()) {
					if (p.getCivetPoints() > 0) {
						n.setText("C:"+p.getCivetPoints()+"pts");
					}
					else {
						n.setText("C");
					}
				}
			}
			
			
			
			return v;
		}
		
	}
	
    @TargetApi(14)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tf = Typeface.createFromAsset(getAssets(), "Cardinal.ttf");
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	ActionBar actionBar = getActionBar();
        	actionBar.setDisplayHomeAsUpEnabled(true);
        	actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        	actionBar.setTitle("");
        }
        
        // set fonts
        Button bt = (Button) findViewById(R.id.validateDiceBtn);
        bt.setTypeface(tf);
        bt = (Button) findViewById(R.id.bevueBtn);
        bt.setTypeface(tf);
        bt = (Button) findViewById(R.id.grelottineBtn);
        bt.setTypeface(tf);
        bt = (Button) findViewById(R.id.civetBtn);
        bt.setTypeface(tf);
        disableCivetButton();
        
        TextView tv = (TextView) findViewById(R.id.lblCurPlayer);
        tv.setTypeface(tf);
        
        tv = (TextView) findViewById(R.id.scoreTitleLbl);
        tv.setTypeface(tf);
        
        if (!CulDeChouette.COMPLETE) {
        	LinearLayout ll = (LinearLayout) findViewById(R.id.customActionLayout);
        	ll.setVisibility(LinearLayout.GONE);
        }
        
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        	ArrayList<CDCPlayer> param = (ArrayList<CDCPlayer>)bundle.getSerializable(CulDeChouette.PLAYERS);
        	if (param != null) {
        		m_listPlayers.addAll(param);
	        	m_titleTV = (TextView) findViewById(R.id.lblCurPlayer);
	        	
	        	// set to -1 at the beginning because nextPlayer is adding 1
	            m_curPlayerIndex = -1;
	            
	            m_adapter = new CDCPlayerGameAdapter (this, android.R.layout.simple_list_item_1, R.id.playerGameRowName, m_listPlayers);
	            setListAdapter(m_adapter);
	            
	            nextPlayer();
        	}
        }
        
    }
    
    @Override
    public void onBackPressed() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// Set an EditText view to get user input 
		final TextView input = new TextView(this);
		input.setText(getString(R.string.CancelGameSentence));
		input.setPadding(5, 5, 5, 5);
		alert.setView(input);
		
		alert.setTitle(getString(R.string.CancelGameLbl));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  cancelGame();
		}
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), null);

		alert.show();
    }
    
    public void cancelGame() {
    	super.onBackPressed();
    }
    
    
   	public boolean onOptionsItemSelected(MenuItem item) {
   	    // Handle item selection
   	    switch (item.getItemId()) {
   	    case android.R.id.home:
   	    	onBackPressed();
   	    default:
   	        return super.onOptionsItemSelected(item);
   	    }
   	}
   	
    
    
    private void nextPlayer() {
    	m_curPlayerIndex = (m_curPlayerIndex + 1) % (m_listPlayers.size());
		m_adapter.notifyDataSetChanged();
		
    	CDCPlayer winner = null;
    	if ((winner = gameHasWinner()) != null) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage(winner.getName() + " " + getString(R.string.WinTheGame) + " " + winner.getPoints() + " " + getString(R.string.PointsLbl) + " !")
        	       .setCancelable(false)
        	       .setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   cancelGame();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
    	}
    	else {
    		CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
    		m_titleTV.setText(curPlayer.getName() + " " + getString(R.string.curPlayer));
    		
    		if (curPlayer.hasCivet()) {
    			enableCivetButton();
    		}
    		else {
    			disableCivetButton();
    		}

        	RadioGroup rg1 = (RadioGroup) findViewById(R.id.dice1group);
        	rg1.clearCheck();
        	RadioGroup rg2 = (RadioGroup) findViewById(R.id.dice2group);
        	rg2.clearCheck();
        	RadioGroup rg3 = (RadioGroup) findViewById(R.id.dice3group);
        	rg3.clearCheck();
    	}
    }
    
    public void ValidateDice(View v) {
    	
    	String v1 = "", v2 = "", v3 = "";
    	
    	RadioGroup rg1 = (RadioGroup) findViewById(R.id.dice1group);
    	int id1selected = rg1.getCheckedRadioButtonId();
    	RadioGroup rg2 = (RadioGroup) findViewById(R.id.dice2group);
    	int id2selected = rg2.getCheckedRadioButtonId();
    	RadioGroup rg3 = (RadioGroup) findViewById(R.id.dice3group);
    	int id3selected = rg3.getCheckedRadioButtonId();

    	if (id1selected == -1 || id2selected == -1 || id3selected == -1) {

        	Toast.makeText(getApplicationContext(), getString(R.string.DiceError), Toast.LENGTH_SHORT).show();
        	return;
        	
    	}
    	
		RadioButton rb1 = (RadioButton)findViewById(id1selected);
		v1 = (String)rb1.getText();
		RadioButton rb2 = (RadioButton)findViewById(id2selected);
		v2 = (String)rb2.getText();
		RadioButton rb3 = (RadioButton)findViewById(id3selected);
		v3 = (String)rb3.getText();
    		
    	int[] combo = getCombo(Integer.parseInt(v1), Integer.parseInt(v2), Integer.parseInt(v3));
    	

    	CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
    	boolean special = (curPlayer.getCivetPoints() > 0 || curPlayer.getGrelottinePoints() > 0);
    	
    	validateCombo(curPlayer, combo, special, false);
    	
    }
    
    public void validateCombo(CDCPlayer curPlayer, int[] combo, boolean special, boolean specialPlayed) {
    	
    	// compute combo
    	switch(combo[0]) {
	    	case CHOUETTE:
	    		if (CulDeChouette.COMPLETE && !specialPlayed) {
	    			displaySirotage(curPlayer, combo);
	    		}
	    		else {
	    			curPlayer.addPoints(CHOUETTE_POINTS[combo[1] - 1]);
	    			displayPointsInfo(curPlayer, combo);
	    		}
	        	break;
	    	case VELUTE:
	        	if (special) {
    				validateSpecialDice(combo, false);
    			}
    			else {
    				curPlayer.addPoints(VELUTE_POINTS[combo[1] - 1]);
    	        	displayPointsInfo(curPlayer, combo);
    			}
	        	break;
	    	case CUL_DE_CHOUETTE:
	        	if (special) {
	        		validateSpecialDice(combo, false);
    			}
    			else {
    				curPlayer.addPoints(CUL_DE_CHOUETTE_POINTS[combo[1] - 1]);
    	        	displayPointsInfo(curPlayer, combo);
    			}
	        	break;
	    	case SUITE:
	    		if (special) {
	    			validateSpecialDice(combo, false);
	    		}
	    		else {
	    			choosePlayerDialog(getString(R.string.SuiteQuestion), DIALOG_SUITE, combo, false);
	    		}
	    		break;
	    	case CHOUETTE_VELUTE:
	    		if (special) {
	    			validateSpecialDice(combo, false);
	    		}
	    		else {
	    			choosePlayerDialog(getString(R.string.ChouetteVeluteQuestion), DIALOG_CHOUETTE_VELUTE, combo, false);
	    		}
	    		break;
	    	default:
	    		if (CulDeChouette.COMPLETE) {
	    			if (special) {
	    				validateSpecialDice(combo, false);
	    			}
	    			else if (!curPlayer.hasGrelottine()) {
	    				curPlayer.setGrelottine(true);
	    				int[] grelottine_combo = { GRELOTTINE, -1 };
	    				displayPointsInfo(curPlayer, grelottine_combo);
	    			}
	    			else {
	    				nextPlayer();
	    			}
	    		}
	    		else {
	    			nextPlayer();
	    		}
	    		break;
    	}
    }

	private void validateSpecialDice(int[] combo, boolean sirotage) {
		CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
		int mult = -1;
		if (combo[0] == CUL_DE_CHOUETTE) {
			if (sirotage == (curPlayer.getCivetBet() == BET_CUL_DE_CHOUETTE_AVEC_SIROTAGE)) {
				mult = 1;
			}
		}
		else if (curPlayer.getCivetBet() == combo[0]) {
			mult = 1;
		}

		if (curPlayer.getCivetPoints() > 0) {
			int points = curPlayer.getCivetPoints();
			curPlayer.addPoints(mult * points);
			curPlayer.setCivetPoints(0);
			curPlayer.setCivetBet(-1);
			curPlayer.setCivet(false);
			String msg = curPlayer.getName() + " " + (mult == 1 ? getString(R.string.PointsWinLbl) : getString(R.string.PointsLoseLbl)) + " " + points + " " +
					getString(R.string.PointsLbl) + " " + getString(R.string.WithCivetLbl);
			displaySpecialEndInfo(msg, curPlayer, points, combo);
			
		}
		else if (curPlayer.getGrelottinePoints() > 0) {
			int points = curPlayer.getGrelottinePoints();
			curPlayer.addPoints(mult * points);
			curPlayer.setGrelottinePoints(0);
			curPlayer.setGrelottineBet(-1);
			CDCPlayer grelottin = m_listPlayers.get(curPlayer.getGrelottin());
			curPlayer.setGrelottin(-1);
			grelottin.addPoints(-1 * mult * points);
			String msg = curPlayer.getName() + " " + (mult == 1 ? getString(R.string.PointsWinLbl) : getString(R.string.PointsLoseLbl)) + " " + points + " " +
					getString(R.string.PointsLbl) + ", " + grelottin.getName() + " " + (mult == 1 ? getString(R.string.GrelottineEndLoose) : getString(R.string.GrelottineEndWin));
			displaySpecialEndInfo(msg, curPlayer, points, combo);
		}
	}


	private void displaySpecialEndInfo(String msg, final CDCPlayer curPlayer, int civetPoints, final int[] combo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(msg)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   validateCombo(curPlayer, combo, false, true);
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}

	private void displaySirotage(final CDCPlayer curPlayer, final int[] combo) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(getString(R.string.SirotageTitle))
    		   .setMessage(curPlayer.getName() + " " + getString(R.string.SirotageQuestion))
    	       .setNegativeButton(getString(R.string.NoLbl), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   boolean special = (curPlayer.getCivetPoints() > 0 || curPlayer.getGrelottinePoints() > 0);
    	        	   if (special) {
    	        		   validateSpecialDice(combo, false);
    	        	   }
    	        	   else {
    	        		   curPlayer.addPoints(CHOUETTE_POINTS[combo[1] - 1]);
        	        	   displayPointsInfo(curPlayer, combo);
    	        	   }
    	           }
    	       })
    	       .setPositiveButton(getString(R.string.YesLbl), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   m_sirotage = new int[6][];
    	        	   for(int i = 0; i < 6; ++i) {
    	        		   m_sirotage[i] = new int[m_listPlayers.size()];
    	        		   Arrays.fill(m_sirotage[i], -1);
    	        	   }
    	        	   sirotageNextBet(curPlayer, -1, combo);
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}
    
    private void sirotageNextBet(CDCPlayer curPlayer, int indBetPlayer, int[] combo) {
    	indBetPlayer++;
    	if (indBetPlayer >= m_listPlayers.size()) {
    		displaySirotageDice(curPlayer, combo);
    	}
    	else {
    		CDCPlayer curBetPlayer = m_listPlayers.get(indBetPlayer);
    		if (curBetPlayer == curPlayer) {
    			sirotageNextBet(curPlayer, indBetPlayer, combo);
    		}
    		else {
    			displaySirotageNextBet(curPlayer, indBetPlayer, combo);
    		}
    	}
    }
    
    private void displaySirotageDice(final CDCPlayer curPlayer, final int[] combo) {
    	final String[] items = { "1", "2", "3", "4", "5", "6"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(curPlayer.getName() + " " + getString(R.string.SirotageDiceQuestion));
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        dialog.dismiss();
    	        sirotageResult(curPlayer, combo, item+1);
    	    }
    	});
    	builder.setCancelable(false);
    	AlertDialog alert = builder.create();
    	alert.show();
	}

	private void displaySirotageNextBet(final CDCPlayer curPlayer, final int indBetPlayer, final int[] combo) {
		final String[] items = getResources().getStringArray(R.array.SirotageBetLbl);

		CDCPlayer betPlayer = m_listPlayers.get(indBetPlayer);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(betPlayer.getName() + " " + getString(R.string.SirotageBetQuestion));
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        dialog.dismiss();
    	        int i = 0;
    	        boolean stop = false;
    	        while(i < m_listPlayers.size() && !stop) {
    	        	if (m_sirotage[item][i] == -1) {
    	        		m_sirotage[item][i] = indBetPlayer;
    	        		stop = true;
    	        	}
    	        	++i;
    	        }
    	        sirotageNextBet(curPlayer, indBetPlayer, combo);
    	    }
    	});
    	builder.setCancelable(false);
    	AlertDialog alert = builder.create();
    	alert.show();
    }

	private void sirotageResult(final CDCPlayer curPlayer, final int[] combo, final int sirotage) {
		String betPlayerName = "";
		int betPlayerNb = 0;
		for (int i = 0; i < m_listPlayers.size(); ++i) {
			if (m_sirotage[sirotage-1][i] != -1) {
				CDCPlayer betPlayer = m_listPlayers.get(m_sirotage[sirotage-1][i]);
				if (betPlayer != null) {
					betPlayer.addPoints(25);
					betPlayerName += (betPlayerNb == 0 ? "" : ", ") + betPlayer.getName();
					betPlayerNb += 1;
				}
			}
		}
		if (betPlayerNb > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(betPlayerName + " " + (betPlayerNb > 1 ? getString(R.string.SirotageBetResultMultiple) : getString(R.string.SirotageBetResultSingle)))
	    	       .setCancelable(false)
	    	       .setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   sirotagePlayerResult(curPlayer, combo, sirotage);
	    	           }
	    	       });
	    	AlertDialog alert = builder.create();
	    	alert.show();
		}
		else {
			sirotagePlayerResult(curPlayer, combo, sirotage);
		}
	}

	private void sirotagePlayerResult(final CDCPlayer curPlayer, final int[] combo,	final int sirotage) {
		boolean special = (curPlayer.getCivetPoints() > 0 || curPlayer.getGrelottinePoints() > 0);
		if (special) {
 		   int[] new_combo = { (sirotage == combo[1] ? CUL_DE_CHOUETTE : CHOUETTE), combo[1] };
 		   validateSpecialDice(new_combo, true);
 		   return;
 	    }
		String msg = "";
		int points;
		if (sirotage == combo[1]) {
			points = CUL_DE_CHOUETTE_POINTS[combo[1] - 1];
    		msg = curPlayer.getName() + " " + getString(R.string.PointsWinLbl) + " " + points + " " 
					+ getString(R.string.PointsLbl) + " " + getString(R.string.SirotagePointsLbl) + " " + combo[1] + " !";
    		curPlayer.addPoints(points);
    	}
		else {
			points = CHOUETTE_POINTS[combo[1] - 1];
    		msg = curPlayer.getName() + " " + getString(R.string.PointsLoseLbl) + " " + points + " " 
					+ getString(R.string.PointsLbl) + " " + getString(R.string.SirotagePointsLbl) + " " + combo[1] 
					+ (combo[1] == 6 ? " " + getString(R.string.SirotagePointsLooseLbl) : "") + " !";
    		curPlayer.addPoints(-1 * points);
			if (combo[1] == 6) {
				curPlayer.setCivet(true);
			}
    	}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(msg)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   nextPlayer();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}

	private CDCPlayer gameHasWinner() {
    	for(CDCPlayer pl : m_listPlayers) {
    		if (pl.getPoints() > 343) {
    			return pl;
    		}
    	}
    	return null;
    }
    
    private int[] getCombo(int d1, int d2, int d3) {
    	int[] res = { -1, -1 };
    	// case CHOUETTE
    	if ( (d1 == d2 && d3 != d1)
    		|| (d1 == d3 && d2 != d1)
    		|| (d2 == d3 && d2 != d1)) {
    		res[0] = CHOUETTE;
    		// get value..
    		if (d1 == d2 || d1 == d3) {
    			res[1] = d1;
    		}
    		else if (d2 == d3) {
    			res[1] = d2;
    		}
    	}
    	// case VELUTE
    	else if ( (d1 + d2 == d3)
    			  || (d1 + d3 == d2)
    			  || (d2 + d3 == d1) ) {
    		res[0] = VELUTE;
    		// get value
    		if (d1 + d2 == d3) {
    			res[1] = d3;
    		}
    		else if (d1 + d3 == d2) {
    			res[1] = d2;
    		}
    		else if (d2 + d3 == d1) {
    			res[1] = d1;
    		}
    	}
    	// case CUL DE CHOUETTE
    	else if (d1 == d2 && d2 == d3) {
    		res[0] = CUL_DE_CHOUETTE;
    		res[1] = d1;
    	}
    	// case CHOUETTE VELUTE
    	if ( (d1 == d2 && d1 + d2 == d3) 
    			 || (d1 == d3 && d1 + d3 == d2) 
    			 || (d2 == d3 && d2 + d3 == d1)) {
    		res[0] = CHOUETTE_VELUTE;
    		//get value
    		if (d1 == d2) {
    			res[1] = d3;
    		}
    		else if (d1 == d3) {
    			res[1] = d2;
    		}
    		else if (d2 == d3) {
    			res[1] = d1;
    		}
    	}
    	// case suite
    	if ( (d1 == d2-1 && d1 == d3-2) 
    			 || (d1 == d3-1 && d1 == d2-2) 
    			 || (d2 == d3-1 && d2 == d1-2) 
    			 || (d2 == d1-1 && d2 == d3-2) 
    			 || (d3 == d2-1 && d3 == d1-2) 
    			 || (d3 == d1-1 && d3 == d2-2) ){
    		res[0] = SUITE;
    	}
    	
    	return res;
    }
    
    public void choosePlayerDialog(String Qlbl, final int which_dialog, final int[] wich_combo, boolean cancelable) {
    	final String[] items = new String[m_listPlayers.size()];
    	for (int i = 0; i < m_listPlayers.size(); ++i) {
    		items[i] = m_listPlayers.get(i).getName();
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(Qlbl);
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        dialog.dismiss();
    	        switch(which_dialog) {
    	        	case DIALOG_SUITE:
    	        		loserSuite(item, wich_combo);
    	        		break;
    	        	case DIALOG_CHOUETTE_VELUTE:
    	        		winnerChouetteVelute(item, wich_combo);
    	        		break;
    	        	case DIALOG_BEVUE:
    	        		loserBevue(item, wich_combo);
    	        		break;
    	        	case DIALOG_GRELOTTINE:
    	        		startGrelottine(item);
    	        		break;
    	        }
    	    }
    	});
    	builder.setCancelable(cancelable);
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    
    public void loserSuite(int ind, int[] which_combo) {
    	
    	
    	CDCPlayer loser = m_listPlayers.get(ind);
    	loser.addPoints(-10);
    	displayPointsInfo(loser, which_combo);
    }
    
    public void winnerChouetteVelute(int ind, int[] which_combo) {
    	CDCPlayer winner = m_listPlayers.get(ind);
    	winner.addPoints(VELUTE_POINTS[which_combo[1] - 1]);
    	displayPointsInfo(winner, which_combo);
    }
    
    public void loserBevue(int ind, int[] bevue_combo) {
    	CDCPlayer loser = m_listPlayers.get(ind);
    	loser.addPoints(-5);
    	displayPointsInfo(loser, bevue_combo);
    }
    
    public void displayPointsInfo(final CDCPlayer pl, int[] combo) {
    	
    	String[] combosLbl = getResources().getStringArray(R.array.CombosLbl);
    	String comboRealised = combosLbl[combo[0]];
    	String msg = "";
    	int points = 0;
    	switch(combo[0]) {
	    	case CHOUETTE:
	    		points = CHOUETTE_POINTS[combo[1] - 1];
	    		msg = pl.getName() + " " + getString(R.string.PointsWinLbl) + " " + points + " " 
						+ getString(R.string.PointsLbl) + " " + getString(R.string.PointsWith) + " " + comboRealised + " " 
						+ getString(R.string.PointsOf) + " " + combo[1] + " !";
	    		break;
	    	case VELUTE:
	    	case CHOUETTE_VELUTE:
	    		points = VELUTE_POINTS[combo[1] - 1];
	    		msg = pl.getName() + " " + getString(R.string.PointsWinLbl) + " " + points + " " 
						+ getString(R.string.PointsLbl) + " " + getString(R.string.PointsWith) + " " + comboRealised + " " 
						+ getString(R.string.PointsOf) + " " + combo[1] + " !";
	    		break;
	    	case CUL_DE_CHOUETTE:
	    		points = CUL_DE_CHOUETTE_POINTS[combo[1] - 1];
	    		msg = pl.getName() + " " + getString(R.string.PointsWinLbl) + " " + points + " " 
						+ getString(R.string.PointsLbl) + " " + getString(R.string.PointsWith) + " " + comboRealised + " " 
						+ getString(R.string.PointsOf) + " " + combo[1] + " !";
	    		break;
	    	case SUITE:
	    		points = 10;
	    		msg = pl.getName() + " " + getString(R.string.PointsLoseLbl) + " " + points + " " 
						+ getString(R.string.PointsLbl) + " " + getString(R.string.PointsWith) + " " + comboRealised + " !";
	    		break;
	    	case BEVUE:
	    		points = 5;
	    		msg = pl.getName() + " " + getString(R.string.PointsLoseLbl) + " " + points + " " 
						+ getString(R.string.PointsLbl) + " " + getString(R.string.PointsWith) + " " + comboRealised + " !";
	    		break;
	    	case GRELOTTINE:
	    		msg = pl.getName() + " " + getString(R.string.PointsWinLbl) + " " + comboRealised + " !";
	    		break;
    	}
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(msg)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   nextPlayer();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    public void diplayBevue(View v) {
    	int[] bevue_combo = { BEVUE, -1 };
    	choosePlayerDialog(getString(R.string.BevueQuestion), DIALOG_BEVUE, bevue_combo, true);
    }
    
    public void grelottineBtnAction(View v) {
    	choosePlayerDialog(getString(R.string.GrelottineStartQuestion), DIALOG_GRELOTTINE, null, true);
    }

    private void startGrelottine(int grelottin_ind) {
    	
    	
    	CDCPlayer grelottin = m_listPlayers.get(grelottin_ind);
    	if (!grelottin.hasGrelottine()) {
    		 Toast.makeText(getApplicationContext(), getString(R.string.Error) + " " + grelottin.getName() + " " + getString(R.string.ErrorGrelottineStart), Toast.LENGTH_SHORT).show();
			 return; 
    	}
    	
    	m_curPlayerIndex -= 1;
    	if (m_curPlayerIndex < 0) {
    		m_curPlayerIndex = m_listPlayers.size() - 1;
    	}
    	
    	if (grelottin_ind == m_curPlayerIndex)  {
    		Toast.makeText(getApplicationContext(), getString(R.string.ErrorAutoGrelottine), Toast.LENGTH_SHORT).show();
    		return; 
    	}
        	
    	
		CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
		m_titleTV.setText(curPlayer.getName() + " " + getString(R.string.curPlayer));
		
		grelottin.setGrelottine(false);
		curPlayer.setGrelottin(grelottin_ind);
		
		disableCivetButton();
		
		displayGrelottinePoints();
	}
    
    private void displayGrelottinePoints() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		
		final CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
		
		CDCPlayer grelottin = m_listPlayers.get(curPlayer.getGrelottin());
		final int maxPoints = Math.abs((int) (Math.min(curPlayer.getPoints(), grelottin.getPoints()) * 0.3));
		
		alert.setTitle(curPlayer.getName() + " " + getString(R.string.GrelottinePointsQuestion));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			try {
			  int value = Integer.parseInt(input.getText().toString());
			  if (value > 0 && value <= maxPoints) {
				  displayChooseBet(value, DIALOG_GRELOTTINE);
			  }
			  else {
				  Toast.makeText(getApplicationContext(), getString(R.string.ErrorGrelottinePoints1) + " " + maxPoints + " " + getString(R.string.ErrorGrelottinePoints2), Toast.LENGTH_SHORT).show();
				  displayGrelottinePoints();
			  }
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), getString(R.string.ErrorGrelottinePoints1) + " " + maxPoints + " " + getString(R.string.ErrorGrelottinePoints2), Toast.LENGTH_SHORT).show();
				displayGrelottinePoints();
			}
		 }
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				CDCPlayer grelottin = m_listPlayers.get(curPlayer.getGrelottin());
				grelottin.setGrelottine(true);
				nextPlayer();
			 }
			  
			});

		alert.show();
    }

	public void civetBtnAction(View v) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		
		final CDCPlayer curPlayer = m_listPlayers.get(m_curPlayerIndex);
		
		alert.setTitle(curPlayer.getName() + " " + getString(R.string.CivetPlayQuestion));

		alert.setPositiveButton(getString(R.string.OkLbl), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			try {
			  int value = Integer.parseInt(input.getText().toString());
			  if (value > 0 && value <= 102) {
				  curPlayer.setCivetPoints(value);
				  displayChooseBet(value, DIALOG_CIVET);
			  }
			  else {
				  Toast.makeText(getApplicationContext(), getString(R.string.ErrorCivetPoints), Toast.LENGTH_SHORT).show();
			  }
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), getString(R.string.ErrorCivetPoints), Toast.LENGTH_SHORT).show();
			}
		 }
		  
		});

		alert.setNegativeButton(getString(R.string.CancerlLbl), null);

		alert.show();
    }
    
    private void displayChooseBet(final int points, final int which_dialog) {
    	final String[] items = getResources().getStringArray(R.array.CombosBet);

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(getString(R.string.CivetPlayBet));
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        dialog.dismiss();
    	        switch(which_dialog) {
    	        	case DIALOG_CIVET:
    	        		setCivetForCurrentPlayer(points, item);
    	        		break;
    	        	case DIALOG_GRELOTTINE:
    	        		setGrelottineForCurrentPlayer(points, item);
    	        		break;
    	        }
    	    }
    	});
    	builder.setCancelable(false);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
    
    private void setCivetForCurrentPlayer(int points, int bet) {
    	CDCPlayer player = m_listPlayers.get(m_curPlayerIndex);
		player.setCivetPoints(points);
		player.setCivetBet(bet);
        m_adapter.notifyDataSetChanged();
    }
    

    private void setGrelottineForCurrentPlayer(int points, int bet) {
    	CDCPlayer player = m_listPlayers.get(m_curPlayerIndex);
		player.setGrelottinePoints(points);
		player.setGrelottineBet(bet);
        m_adapter.notifyDataSetChanged();
    }
    
    private void enableCivetButton() {
    	Button bt = (Button) findViewById(R.id.civetBtn);
    	bt.setTextColor(Color.WHITE);
        bt.setEnabled(true);
    }
    
    private void disableCivetButton() {
    	Button bt = (Button) findViewById(R.id.civetBtn);
    	bt.setTextColor(Color.LTGRAY);
        bt.setEnabled(false);
    }
}
