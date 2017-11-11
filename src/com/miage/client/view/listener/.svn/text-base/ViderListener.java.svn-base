package com.miage.client.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.miage.client.view.IHM;

public class ViderListener implements ActionListener {

	/**
	 * 
	 */
	private final IHM ihm;

	/**
	 * @param ihm
	 */
	public ViderListener(IHM ihm) {
		this.ihm = ihm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == ihm.boutonloginVider){
			this.ihm.viderFicheLogin();
		}
		
		if(e.getSource() == ihm.boutonCreateAccountVider){
			this.ihm.viderFicheCreationCompte();
		}
	}                
}