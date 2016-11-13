package com.pierral.cdc;

import java.io.Serializable;


public class CDCPlayer implements Serializable {


	private static final long serialVersionUID = -3830370920397933922L;
	
	private String mName;
	private int mPoints;
	private boolean mHasCivet;
	private int mCivetPoints;
	private int mCivetBet;
	private boolean mHasGrelottine;
	private int mGrelottinePoints;
	private int mGrelottineBet;
	private int mIndGrelottin;
	
	/**
	 * public constructor
	 * @param name Player name
	 */
	public CDCPlayer(String name) {
		setName(name);
		setPoints(0);
		setCivet(false);
		setCivetPoints(0);
		setCivetBet(-1);
		setGrelottine(false);
		setGrelottinePoints(0);
		setGrelottineBet(-1);
		setGrelottin(-1);
	}
	
	/**
	 * toString method..
	 */
	public String toString() {
		return mName + " - " + mPoints + "pts";
	}

	/**
	 * @return the Name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name the Name to set
	 */
	public void setName(String name) {
		this.mName = name;
	}

	/**
	 * @return the Points
	 */
	public int getPoints() {
		return mPoints;
	}

	/**
	 * @param points the Points to set
	 */
	public void setPoints(int points) {
		this.mPoints = points;
	}
	
	/**
	 * @param pointsToAdd points to add to the player
	 */
	public void addPoints(int pointsToAdd) {
		this.mPoints += pointsToAdd;
	}
	
	/**
	 * @param pointsToSub points to substract to the player
	 */
	public void substractPoints(int pointsToSub) {
		this.mPoints -= pointsToSub;
	}

	/**
	 * @return the Civet
	 */
	public boolean hasCivet() {
		return mHasCivet;
	}

	/**
	 * @param Civet the Civet to set
	 */
	public void setCivet(boolean civet) {
		this.mHasCivet = civet;
	}
	
	/**
	 * set points for civet
	 * @param points
	 */
	public void setCivetPoints(int points) {
		this.mCivetPoints = points;
	}
	
	/**
	 * get points for civet
	 * @return int
	 */
	public int getCivetPoints() {
		return this.mCivetPoints;
	}

	/**
	 * set civet bet
	 * @param bet
	 */
	public void setCivetBet(int bet) {
		this.mCivetBet = bet;
	}
	
	/**
	 * get civet bet
	 * @return int
	 */
	public int getCivetBet() {
		return this.mCivetBet;
	}
	
	/**
	 * @return the Grelottine
	 */
	public boolean hasGrelottine() {
		return mHasGrelottine;
	}

	/**
	 * @param Grelotine the Grelottine to set
	 */
	public void setGrelottine(boolean grelottine) {
		this.mHasGrelottine = grelottine;
	}
	

	/**
	 * set points for grelottine
	 * @param points
	 */
	public void setGrelottinePoints(int points) {
		this.mGrelottinePoints = points;
	}
	
	/**
	 * get points for grelottine
	 * @return int
	 */
	public int getGrelottinePoints() {
		return this.mGrelottinePoints;
	}

	/**
	 * set grelottine bet
	 * @param bet
	 */
	public void setGrelottineBet(int bet) {
		this.mGrelottineBet = bet;
	}
	
	/**
	 * get grelottine bet
	 * @return int
	 */
	public int getGrelottineBet() {
		return this.mGrelottineBet;
	}
	

	/**
	 * set grelottin index
	 * @param bet
	 */
	public void setGrelottin(int index) {
		this.mIndGrelottin = index;
	}
	
	/**
	 * get grelottin index
	 * @return int
	 */
	public int getGrelottin() {
		return this.mIndGrelottin;
	}
}
