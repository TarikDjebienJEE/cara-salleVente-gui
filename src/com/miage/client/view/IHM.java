package com.miage.client.view;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.miage.client.view.listener.InscriptionListener;
import com.miage.client.view.listener.LoginListener;
import com.miage.client.view.listener.ViderListener;
import com.miage.ejb.IArticleServiceRemote;
import com.miage.ejb.IUtilisateurServiceRemote;
import com.miage.ejb.exception.UserAlreadyExistException;
import com.miage.ejb.exception.UserBadLoginException;
import com.miage.entity.Article;
import com.miage.entity.User;


/**
 * Interface graphique de notre application de salle de vente
 * @author tarik
 * @author eric
 */
public class IHM extends JFrame {

	private IUtilisateurServiceRemote utilisateurServiceRemote;
	private IArticleServiceRemote articleServiceRemote;	

	private User currentUser ; 
	private Article articleSelectionne ; 	
	
	private static final long serialVersionUID = 1L;
	private static final String WINDOWS_TITLE = "Salle des ventes";
	private static final Dimension WINDOWS_DIMENSION = new Dimension(800, 600);

	private static final String MESSAGE_PROBLEME_DEFAULT = "Aucun probleme a signaler.";
	private static final String MESSAGE_PERSONNE_EXISTE_DEJA = "Cette personne existe déjà.";
	private static final String MESSAGE_ERROR_LOGIN = "Login/Password invalide";

	//******* COMPOSANTES DE L'INTERFACE HOMME MACHINE.*********

	private final CardLayout cl = new CardLayout();

	/**
	 * Un tableau stockant les differentes vue possibles
	 */
	private String[] listContent = {"LOGIN_VUE", "MAIN_VUE"};

	/**
	 * Les ecrans
	 */
	protected JTabbedPane onglet;

	// VIEW

	/**
	 * Container login de l'IHM
	 */
	protected JPanel loginScreen;

	/**
	 * Panel pour les boutons de login
	 */
	protected JPanel loginBoutons;

	/**
	 * Panel pour les boutons de creation de compte
	 */
	protected JPanel createAccountBoutons;

	/**
	 * Panel pour la fiche login
	 */
	protected JPanel panelFicheLogin;

	/**
	 * Panel pour la fiche de creation de compte utilisateur
	 */
	protected JPanel panelFicheCreateAccount;

	
	/**
	 * Panel pour la proposition d'un article a la vente 
	 */
	protected JPanel panelPropositionArticle;	
	
	
	/**
	 * Panel pour la proposition d'une enchere
	 */
	protected JPanel panelPropositionEnchere;	
	
	
	/**
	 * Panel pour l'observation enchere
	 */
	protected JPanel panelObservationEnchere;	
	
	
	/**
	 * Panel pour la cloture d'une vente
	 */
	protected JPanel panelClotureVente;		
	
	// INPUT

	/** 
	 * Login de connexion
	 */
	protected JTextField champLogin;

	/** 
	 * Password pour la connexion
	 */
	protected JTextField champPassword;

	/** 
	 * Champ nom de creation de compte utilisateur
	 */
	protected JTextField champNom;

	/** 
	 * Champ adresse de creation de compte utilisateur
	 */
	protected JTextField champAdresse;

	/** 
	 * Champ mail de creation de compte utilisateur
	 */
	protected JTextField champMail;

	/** 
	 * Champ mot de passe de creation de compte utilisateur
	 */
	protected JTextField champNewPassword;

	
	
	/** 
	 * Champ pour la description de l'article
	 */
	protected JTextField champDescriptionArticleVente;
	
	/** 
	 * Champ pour le prix de l'article
	 */	
	protected JTextField champPrixArticleVente;
	
	
	/** 
	 * Combobox pour le choix d'un article
	 */	
	protected JComboBox comboArticlesEnVente ;
	
	
	/** 
	 * Permet d'afficher l'enchere courante
	 */
	protected JTextField inputEnchereCourante;	
	
	
	/** 
	 * Permet d'afficher l'enchere proposee
	 */
	protected JTextField inputNouvelleEnchere;	
		
	
	// BOUTONS

	/** 
	 * Le bouton vider clear l'affichage sur les champs de connexion
	 */
	public JButton boutonloginVider;

	/** 
	 * Le bouton connection pour se connecter à l'application sur l'IHM
	 */
	public JButton boutonloginConnection;

	/** 
	 * Le bouton vider clear l'affichage sur les champs de creation d'un compte
	 */
	public JButton boutonCreateAccountVider;

	/** 
	 * Le bouton creationCompte pour creer un compte
	 */
	public JButton boutonCreationCompte;
	
	
	/** 
	 * Le bouton pour confirmer la vente de l'article
	 */
	public JButton boutonVendreArticle;	

	
	/** 
	 * Le bouton pour rafraichir la liste des articles en vente
	 */
	public JButton boutonRefreshArticlesEnVente;	
	
	
	/** 
	 * Le bouton pour selectionner un article
	 */
	public JButton boutonSelectionArticlesEnVente;	
		
	
	
	/** 
	 * Le bouton pour valider un enchere
	 */
	public JButton boutonValiderEnchere;	
			
	
	// LABEL

	/**
	 *  Label pour afficher les problemes.
	 */
	protected JLabel labelProblemeLogin;

	/**
	 * Constructeur d'initialisation de notre IHM
	 * 
	 * @param repertoireControler le controller vers lequel on delegue les requetes clientes
	 */
	public IHM() {

		initEJB();

		initIHMConfiguration();

		initFicheLogin();
		
		initIHMOnglet();

		initBoutonsLogin();

		initMessage();

		pack();
	}

	private void initEJB() {
		// creation du "contexte initial" = de la connexion a l'annuaire du serveur
		InitialContext context;
		try {
			context = new InitialContext();
			utilisateurServiceRemote = (IUtilisateurServiceRemote) context.lookup("UtilisateurService") ;
			articleServiceRemote = (IArticleServiceRemote) context.lookup("ArticleService") ;
		} catch (NamingException e) {
			Logger.getLogger(IHM.class.getName()).log(Level.SEVERE, "Impossible de se connecter à l'annuaire du serveur de salle de vente", e);
		}
	}

	/**
	 * Initialisation de la zone de message de notification
	 */
	private void initMessage() {

		this.labelProblemeLogin = new JLabel("");
		this.loginScreen.add(BorderLayout.SOUTH, labelProblemeLogin);

		// Fixer l'erreur avec le message par defaut.
		fixerErreur(null);
	}

	private void initFicheLogin() {
		this.panelFicheLogin = new JPanel();
		panelFicheLogin.setSize(200, 200);
		this.panelFicheLogin.setLayout(new BorderLayout());

		this.panelFicheCreateAccount = new JPanel();
		panelFicheCreateAccount.setSize(200, 200);
		this.panelFicheCreateAccount.setLayout(new BorderLayout());

		this.loginScreen.add(BorderLayout.WEST, panelFicheLogin);
		this.loginScreen.add(BorderLayout.EAST, panelFicheCreateAccount);

		// Fiche de login
		JPanel pc1 = new JPanel();
		pc1.setLayout(new GridLayout(2,2));
		this.panelFicheLogin.add(BorderLayout.NORTH, pc1);

		pc1.add(new JLabel("Login : "));
		this.champLogin = new JTextField(14);
		champLogin.setSize(100, 20);
		pc1.add(champLogin);

		pc1.add(new JLabel("Password : "));
		this.champPassword = new JPasswordField(14);
		champPassword.setSize(100, 20);
		pc1.add(champPassword);

		// Fiche de creation de compte
		JPanel pc2 = new JPanel();
		pc2.setLayout(new GridLayout(4,2));
		this.panelFicheCreateAccount.add(BorderLayout.NORTH, pc2);

		pc2.add(new JLabel("Nom : "));
		this.champNom = new JTextField(14);
		champNom.setSize(100, 20);
		pc2.add(champNom);

		pc2.add(new JLabel("Adresse : "));
		this.champAdresse = new JTextField(14);
		champAdresse.setSize(100, 20);
		pc2.add(champAdresse);

		pc2.add(new JLabel("Mail : "));
		this.champMail = new JTextField(14);
		champMail.setSize(100, 20);
		pc2.add(champMail);

		pc2.add(new JLabel("Password : "));
		this.champNewPassword = new JTextField(14);
		champNewPassword.setSize(100, 20);
		pc2.add(champNewPassword);
	}

	private void initBoutonsLogin() {
		this.loginBoutons = new JPanel();
		this.loginBoutons.setLayout(new GridLayout(1,2));
		this.panelFicheLogin.add(BorderLayout.SOUTH, loginBoutons);

		this.createAccountBoutons = new JPanel();
		this.createAccountBoutons.setLayout(new GridLayout(1,2));
		this.panelFicheCreateAccount.add(BorderLayout.SOUTH, createAccountBoutons);

		LoginListener loginListener = new LoginListener(this);
		this.boutonloginConnection = new JButton("Se connecter");
		this.loginBoutons.add(boutonloginConnection);
		this.boutonloginConnection.addActionListener(loginListener);

		ViderListener viderListener = new ViderListener(this);
		this.boutonloginVider = new JButton("Reinitialiser");
		this.loginBoutons.add(boutonloginVider);
		this.boutonloginVider.addActionListener(viderListener);

		InscriptionListener createAccountListener = new InscriptionListener(this);
		this.boutonCreationCompte = new JButton("S'inscrire");
		this.createAccountBoutons.add(boutonCreationCompte);
		this.boutonCreationCompte.addActionListener(createAccountListener);

		this.boutonCreateAccountVider = new JButton("Reinitialiser");
		this.createAccountBoutons.add(boutonCreateAccountVider);
		this.boutonCreateAccountVider.addActionListener(viderListener);

	}

	/**
	 * Initialisation des parametres de la Fenetre d'affichage
	 */
	private void initIHMConfiguration() {
		this.setSize(WINDOWS_DIMENSION);
		this.setTitle(WINDOWS_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLayout(cl);

		this.onglet = new JTabbedPane(JTabbedPane.NORTH);

		this.loginScreen = new JPanel();
		this.loginScreen.setLayout(new BorderLayout());

		this.getContentPane().add(loginScreen, listContent[0]);
		this.getContentPane().add(onglet, listContent[1]);

		this.setVisible(true);
	}

	
	/**
	 * Initialisation des parametres de la Fenetre d'affichage
	 */
	private void initIHMOnglet() {
		panelPropositionArticle = new JPanel();
		panelPropositionEnchere = new JPanel();
		panelObservationEnchere = new JPanel();
		panelClotureVente = new JPanel();
		
		this.onglet.add("Proposition article à la vente", panelPropositionArticle);
		this.onglet.add("Proposition enchere" , panelPropositionEnchere);
		this.onglet.add("Observation enchere", panelObservationEnchere);
		this.onglet.add("Cloture vente" , panelClotureVente);
		
		initPanelPropositionArticle();
		initPanelPropositionEnchere();
		initPanelObservationEnchere();
		initPanelClotureVente();
		
	}	


	//******* METHODES ********

	/**
	 *  Creation panelClotureVente
	 */
	private void initPanelClotureVente() {
		
	}

	/**
	 *  Creation panelObservationEnchere
	 */	
	private void initPanelObservationEnchere() {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  Creation panelPropositionEnchere
	 */	
	private void initPanelPropositionEnchere() {
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(4,1));
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1,4));
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(1,2));
		
		final JLabel messageValidationEnchere = new JLabel();
		comboArticlesEnVente  = new JComboBox();
		boutonRefreshArticlesEnVente = new JButton("Rafraichir la liste");
		boutonSelectionArticlesEnVente  = new JButton("Surrencherir cet article");
		boutonValiderEnchere  = new JButton("Valider enchere");
		inputEnchereCourante = new JTextField();
		inputEnchereCourante.setEditable(false);
		inputNouvelleEnchere = new JTextField();
		
		boutonSelectionArticlesEnVente.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    articleSelectionne = (Article) comboArticlesEnVente.getSelectedItem();
				String valuePrice = articleSelectionne.getPrixVente().toString();
				inputEnchereCourante.setText(valuePrice);
			}
		});
		
		boutonRefreshArticlesEnVente.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Article> articlesEnVente =  articleServiceRemote.getAllArticlesEnVente();
				for (Article article : articlesEnVente) {
					comboArticlesEnVente.addItem(article);
				}
			}
		});
		
		boutonValiderEnchere.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Float.valueOf(inputEnchereCourante.getText()) >= Float.valueOf(inputNouvelleEnchere.getText())){
					messageValidationEnchere.setText("Le montant de l'enchere proposee doit etre superieur a la derniere enchere") ;
					return ;
				}
				utilisateurServiceRemote.surrencherir(currentUser.getNom(), articleSelectionne.getId(), Float.valueOf(inputNouvelleEnchere.getText()));
				
			}
		});
		
		
		panelPropositionEnchere.setLayout(new BorderLayout());
		panelPropositionEnchere.add(BorderLayout.WEST,p1);
		panelPropositionEnchere.add(BorderLayout.NORTH,p2);
		panelPropositionEnchere.add(BorderLayout.SOUTH,p3);
		
		p2.add(new JLabel("Liste des articles en cours de vente : "));
		p2.add(comboArticlesEnVente);
		p2.add(boutonRefreshArticlesEnVente);
		p2.add(boutonSelectionArticlesEnVente);
		
		p1.add(new JLabel("Enchere courante : "));
		p1.add(inputEnchereCourante);
		p1.add(new JLabel("Proposer enchere : "));
		p1.add(inputNouvelleEnchere);
		
		p3.add(boutonValiderEnchere);
		p3.add(messageValidationEnchere);
	}

	/**
	 *  Creation panelPropositionArticle
	 */
	private void initPanelPropositionArticle() {
		// Fiche de vente d'un article
		panelPropositionArticle.setLayout(new GridLayout(3,2));

		panelPropositionArticle.add(new JLabel("Description : "));
		this.champDescriptionArticleVente = new JTextField(30);
		champDescriptionArticleVente.setSize(100, 50);
		panelPropositionArticle.add(champDescriptionArticleVente);
		
		
		panelPropositionArticle.add(new JLabel("Montant minimale de vente : "));
		this.champPrixArticleVente = new JTextField(30);
		champPrixArticleVente.setSize(100, 50);
		panelPropositionArticle.add(champPrixArticleVente);		
		
		boutonVendreArticle = new JButton();
		boutonVendreArticle.setText("Vendre article");
		panelPropositionArticle.add(boutonVendreArticle);
		
		boutonVendreArticle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nomVendeur = currentUser.getNom() ; 
				String description = champDescriptionArticleVente.getText();
				Float prixVente = Float.valueOf(champPrixArticleVente.getText());
				articleServiceRemote.ajouterArticle(nomVendeur, description, prixVente);
			}
		});
		
	}

	/**
	 *  Fixer le message d'erreur.
	 *  Si message vide alors afficher le message par defaut.
	 */
	public void fixerErreur (String message) {
		if (message != null && message.length() != 0) {
			this.labelProblemeLogin.setText(message + " !");
		} else {
			this.labelProblemeLogin.setText(MESSAGE_PROBLEME_DEFAULT);
		}
	}

	/**
	 * Lorsque le bouton Vider est selectionne alors cette methode
	 * remet a blanc les champs de la fiche login
	 */
	public void viderFicheLogin() {
		Logger.getLogger(IHM.class.getName()).log(Level.INFO, "IHM.viderFicheLogin()");
		this.champLogin.setText("");
		this.champPassword.setText("");
	}

	public void inscription() {

		if (champNom.getText().isEmpty() || champNewPassword.getText().isEmpty()) {
			fixerErreur("Vous devez remplir les champs nom, password pour creer un compte.");
			return;
		}

		Logger.getLogger(IHM.class.getName()).log(Level.INFO, "IHM.seConnecter()");
		try {
			utilisateurServiceRemote.inscrire(champNom.getText(), champAdresse.getText(), champMail.getText(), champNewPassword.getText());
		} catch (UserAlreadyExistException e) {
			fixerErreur(MESSAGE_PERSONNE_EXISTE_DEJA);
		}
	}

	public void identification() {

		if (champLogin.getText().isEmpty() || champPassword.getText().isEmpty()) {
			fixerErreur("Vous devez remplir les champs login, password pour vous connecter.");
			return;
		}

		Logger.getLogger(IHM.class.getName()).log(Level.INFO, "IHM.identification()");
		try {
			currentUser = utilisateurServiceRemote.identifier(champLogin.getText(), champPassword.getText());
			cl.show(this.getContentPane(), listContent[1]);
			fixerErreur(null);
		} catch (UserBadLoginException e) {
			fixerErreur(MESSAGE_ERROR_LOGIN);
		}
	}

	/**
	 * Lorsque le bouton Vider est selectionne alors cette methode
	 * remet a blanc les champs de la fiche de creation d'un compte
	 */
	public void viderFicheCreationCompte() {
		Logger.getLogger(IHM.class.getName()).log(Level.INFO, "IHM.viderFicheCreationCompte()");
		this.champNom.setText("");
		this.champAdresse.setText("");
		this.champMail.setText("");
		this.champNewPassword.setText("");
	}

}
