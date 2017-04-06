package view;

import java.util.*;

import com.sun.javafx.geom.Rectangle;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import view.toolbar.ToolBarView;


/**
 * 
 * @author Jonathan
 *
 */
public class GUIBuilder {
	private static final double SCREEN_HEIGHT = 650;
	private static final double SCREEN_WIDTH = 1000;
	private static final double TURTLE_VIEW_HEIGHT_FACTOR = .6;
	private static final double TURTLE_VIEW_WIDTH_FACTOR = .8;
	private static final double TAB_VIEW_HEIGHT_FACTOR = .8;
	private static final double TAB_VIEW_WIDTH_FACTOR = .2;
	private static final double TITLE_PANE_HEIGHT_FACTOR = .2;
	private static final double TITLE_PANE_WIDTH_FACTOR = .375;
	private static final double COMMAND_LINE_HEIGHT_FACTOR = .2;
	//private static final String DEFAULT_RESOUCES = "resources/screentext/English";
	//private static final String DEFAULT_CSS = "resources/css/slogo.css";
	
	/**
	 * Resource bundle for all text displayed on screen.
	 */
	//public static final ResourceBundle myResources = ResourceBundle.getBundle(DEFAULT_RESOUCES);

	private Collection<GUIComponent> myComp = new ArrayList<GUIComponent>();

	private GUIComponent grid;
	private GUIComponent tab;
	private GUIComponent toolbar;
	
	/**
	 * Initializes the main Scene and Stage.
	 */
	public GUIBuilder(UtilityFactory utilF){
		grid = new GridView(utilF);
		tab = new TabView(utilF);
		toolbar = new ToolBarView(utilF);
		myComp.add(grid);
		myComp.add(tab);
		myComp.add(toolbar);
	}
	
	public Pane buildPane() {
		BorderPane myPane = new BorderPane();
		myPane.setPrefHeight(SCREEN_HEIGHT);
		myPane.setPrefWidth(SCREEN_WIDTH);
//		for(GUIComponent c: myComp){
//			myPane.getChildren().add(c.buildComponent());
//		}
		myPane.setTop(toolbar.buildComponent());
		myPane.setRight(tab.buildComponent());
		myPane.setLeft(grid.buildComponent());
		return myPane;
	}

	public Scene buildScene() {
		Scene myScene = new Scene(buildPane());
		return myScene;
	}
}
