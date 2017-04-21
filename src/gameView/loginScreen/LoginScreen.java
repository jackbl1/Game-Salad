package gameView.loginScreen;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import gameView.AbstractViewer;
import gameView.ICommandView;
import gameView.UIView;
import gameView.commands.RegisterCommand;
import gameView.commands.SignInCommand;
import gameView.tools.FrontEndException;
import gameView.tools.ResourceRetriever;

public class LoginScreen extends AbstractViewer {
	
	private final String myName = "LoginScreen";
	
	private HBox myMain;
	private VBox myLeft;
	private VBox myRight;
	private Scene myScene;
	
	public LoginScreen(UIView view) { 
		super(view);
		myLeft = new VBox(10);
		myRight = new VBox(10); 
		makeScene();
	}
 
	@Override 
	public Scene getScene() {    
		return myScene;
	}
	
	private void makeScene() {
		makeLeft();
		makeRight();
		myMain = new HBox(myLeft, myRight);
		myMain.setId("main");
		myScene = new Scene(myMain, UIView.DEFAULT_SIZE.width, UIView.DEFAULT_SIZE.height);
		myScene.getStylesheets().add(new ResourceRetriever().getStyleSheets(this,myName));
	}
	
	private void makeLeft() {
		Label lab = makeLabel("Register", "register");
		setMargin(lab, 0, 0, 20, 0);
		TextField username = makeInput("User Name", "username");
		TextField password = makeInput("Password", "passworrd"); 
		TextField confirmPass = makeInput("Re-enter Password", "passwordcheck");
		Button register = makeButton(new RegisterCommand((ICommandView) getView()));
		setMargin(register, 20, 0, 0, 0);
		setBox(myLeft, "left", lab, username, password, confirmPass, register);
	}
	
	private void makeRight() {
		Label lab = makeLabel("Sign In", "signin");
		setMargin(lab, 0, 0, 20, 0);
		TextField username = makeInput("User Name", "username"); 
		TextField password = makeInput("Password", "password");
		//Button signIn = makeButton(new SignInCommand((ICommandView) getView())); 
		Button signIn = new Button("test");
		signIn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					new FaceBookLogin().login();
				} catch (IOException e) {
					throw new FrontEndException("FB FAILURE");
				}
			}
		});
		setMargin(signIn, 20, 0, 0, 0);
		setBox(myRight, "right", lab, username, password, signIn);
	}
	
	private void setMargin(Node node, int top, int left, int bottom, int right) {
		VBox.setMargin(node, new Insets(top, left, bottom, right));
	}
	private void setBox(VBox box, String id, Node... args) { 
		box.setAlignment(Pos.CENTER);
		box.setId(id);
		box.getChildren().addAll(Arrays.asList(args));  
		box.setPrefWidth(UIView.DEFAULT_SIZE.width/2);
		box.setPrefWidth(UIView.DEFAULT_SIZE.height);
		
	}
	
	private TextField makeInput(String description, String id) {
		TextField text = new TextField(description);
		text.setId(id);
		return text;
	}
	
}
