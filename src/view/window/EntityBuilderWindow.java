package view.window;

import java.io.File;
import java.util.ArrayList;
import components.entityComponents.SpriteComponent;
import entity.Entity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.GUIBuilder;
import view.ImageChooser;
import view.UtilityFactory;
import view.ViewData;

public class EntityBuilderWindow implements IWindow{

	private ObservableList<Entity> blocksList;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private Image myImageImage = new Image(getClass().getClassLoader().getResourceAsStream("empty.jpg"));
	private ImageView myImage = new ImageView(myImageImage);
	private String myImagePath = "";
	private Entity myEntity;
	private ImageChooser imageChooser = new ImageChooser();
	private UtilityFactory util;
	private ViewData myData;
	private Stage myStage = new Stage();
	private int i = 0;
	private String[] entityList = {"Error"};

	public EntityBuilderWindow(UtilityFactory utilIn, ObservableList<Entity> blocksListIn, ViewData dataIn) {
		myData = dataIn;
		blocksList = blocksListIn;
		util = utilIn;
		nodeList.add(myImage);
	}

	public void showEntityBuilder() {
		myStage.setScene(buildScene());
		myStage.show();
	}

	private Scene buildScene() {
		buildNodes();
		Pane pane = buildPane();
		Scene myScene = new Scene(pane, 350, 400);
		myScene.getStylesheets().add(GUIBuilder.RESOURCE_PACKAGE + GUIBuilder.STYLESHEET);
		return myScene;
	}

	public ImageView getImage() {
		return myImage;
	}

	public Entity getEntity() {
		return myEntity;
	}

	private void buildNodes() {
		Node imageButton = util.buildButton("ChooseImageLabel", e -> {
			myImagePath = imageChooser.chooseFile();
			System.out.println();
			Image image = new Image(System.getProperty("user.dir") + File.separator + "images"+ File.separator + myImagePath);
			myImage.setImage(image);
			myImage.setFitWidth(200);
			myImage.setFitHeight(200);
		});
		nodeList.add(imageButton);

		Node okayButton = util.buildButton("OkayLabel", e -> {
			Entity tempEntity = new Entity(i);
			i++;
			tempEntity.addComponent(new SpriteComponent(myImagePath));
			//myData.defineEntity(tempEntity);
			//myData.setUserSelectedEntity(tempEntity);
			myStage.close();
			EntityConfigurationWindow ecw = new EntityConfigurationWindow(util, myData, entityList, tempEntity);
			ecw.show();
		});

		Node entityType = new Label("Kind of Entity");
		nodeList.add(entityType);

		final ToggleGroup group = util.buildRadioButtonGroup("SelectEntityType", nodeList);
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				entityList = (String[]) new_toggle.getUserData();
				for(String s: entityList){
					System.out.println(s);
				}
			}
		});
		
		nodeList.add(okayButton);
	}

	private Pane buildPane() {
		VBox pane = new VBox();
		pane.getChildren().addAll(nodeList);
		return pane;
	}

	@Override
	public void openWindow() {
		myStage.show();
	}

}