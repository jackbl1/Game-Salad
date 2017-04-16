package controller;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.regexp.internal.recompile;

import components.entityComponents.ComponentType;
import components.movementcomponents.LocationComponent;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.stage.Stage;
import javafx.util.Duration;
import entity.restricted.IRestrictedEntity;
import entity.restricted.IRestrictedEntityManager;
import gameEngine_interface.GameEngine;
import gameView.Coordinate;
import gameView.observers.ObserverManager;
import gameView_interfaces.UIViewInterface;
import gamedata.GameData;

/**
 *
 * @author Jacob
 *
 */
public class WorldAnimator{
    // private Stage myStage;
    public static final int FRAMES_PER_SECOND = 45;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final int KEY_INPUT_SPEED = 3;
    public static final int WIDTH = 5000;
    public static final int LENGTH = 1000;
    
    private Set<KeyCode> keysPressed = new HashSet<KeyCode>();
    private Set<KeyCode> keysReleased = new HashSet<KeyCode>();


    private Scene myScene;
    private Timeline animation;
    private Group root;
    //private GameBuilder myGameBuilder;
    private Camera myCamera;
    private UIViewInterface myView;
    private	ObserverManager myObservers;
    
    private Map<Integer, ImageView> imageMap= new HashMap<Integer, ImageView>();

    private boolean pause = false;

    public WorldAnimator(UIViewInterface view){
    	myView = view;
    }

    public void start (GameData myData){
        root = new Group();
        IRestrictedEntityManager restrictedEntityManager = myData.getRestrictedEntityManager();
        myObservers = new ObserverManager(this, restrictedEntityManager);
        //myGameBuilder = new GameBuilder();
//        myScene = myGameBuilder.setUpGame(root, restrictedEntityManager, 500,500);

//BELALS SHIT

        //myScene = myGameBuilder.setUpGame(root, restrictedEntityManager, 500,500);
        //myScene = new Scene(root,LENGTH,WIDTH);
        //myScene = new Scene(root,LENGTH - 200,WIDTH);
        LocationComponent lc = myData.mainLocation();
        myCamera = new Camera(LENGTH ,myScene, lc);
        fillMapAndDisplay();
        for (Integer id : imageMap.keySet()) {
            root.getChildren().add(imageMap.get(id));
        }

        myScene.setOnKeyPressed(e -> handleKeyPressed(e.getCode()));
        myScene.setOnKeyReleased(e -> handleKeyReleased(e.getCode()));
        
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                e-> step(SECOND_DELAY));
        this.animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        //animation.play();
    }
    
    public Scene getScene() {
        return myScene;
    }

    /*private void createMap(IRestrictedEntityManager manager) {
        Collection<IRestrictedEntity> entities = manager.getRestrictedEntities();
        for (IRestrictedEntity e: entities){
            String[] test = e.getRestrictedImagePath().split("[\\\\/]");
            imageMap.put(e.getID(), new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(test[test.length-1]))));
            imageMap.get(e.getID()).setX(e.getRestrictedLocation().getWidth());
            imageMap.get(e.getID()).setY(e.getRestrictedLocation().getHeight());
            imageMap.get(e.getID()).setFitHeight(e.getRestrictedIPComponent().getHeight());
            imageMap.get(e.getID()).setFitWidth(e.getRestrictedIPComponent().getWidth());
            //imageMap.get(e.getID()).setTranslateX(e.getLocation().getX()*50-475);
			//imageMap.get(e.getID()).setTranslateY(e.getLocation().getY()*50-175);
        }
    }*/

    private void step(double elapsedTime){
    	myView.step(keysPressed);
        fillMapAndDisplay();
        /*VelocityComponent velocityComponent = (VelocityComponent) myGameEngine.getMainCharacter().getComponent(ComponentType.Velocity);
        updateScrolling(locationComponent, velocityComponent);*/
        myCamera.updateCamera();
    }

    
    private void handleKeyReleased(KeyCode keyCode) {
        keysReleased.add(keyCode);
        keysPressed.remove(keyCode);
        System.out.println("IN WORLD ANIMATOR" + keyCode);
    }

    private void handleKeyPressed(KeyCode keyCode) {
        externalKeyHandler(keyCode);
        keysPressed.add(keyCode);
    }
    
    private void externalKeyHandler(KeyCode code){
        if(code == KeyCode.P && !pause){
            animation.pause();
            pause=true;
        }
        else if (code == KeyCode.P && pause) {
            animation.play();
            pause =false;
        }
    }

    public void setKeys(Scene s) {
        s.setOnKeyPressed(e -> handleKeyPressed(e.getCode()));
        s.setOnKeyReleased(e -> handleKeyReleased(e.getCode()));
    }

    private void fillMapAndDisplay(){
    	
    	//MYOBSERVERS RETURNS COLLECTION OF IMAGEVIEW THAT ARE CORRECTLY UPDATED
    	Map<Integer, ImageView> entities = myObservers.getEntityMap();
        //HashMap<Integer, ImageView> map = new HashMap<Integer, ImageView>();
        for(Integer entity : entities.keySet()){
            //SequentialTransition trans = new SequentialTransition();
            removeEntity(entity,entities);
            //updateEntity(entity,entities);
            createEntity(entity,entities);
        }
    }
    
    private void removeEntity(Integer entity, Map<Integer, ImageView> entities){
        if(entities.get(entity) == null){
            if (imageMap.containsKey(entity)){
                root.getChildren().remove(imageMap.get(entity));
                imageMap.remove(entity);
            }
        }
    }

private void createEntity(Integer entity, Map<Integer, ImageView> entities){
        if (!imageMap.containsKey(entity)){
            ImageView imageView = entities.get(entity);
            imageMap.put(entity, imageView);
            root.getChildren().add(imageMap.get(entity));
        }
    }
  /*  private void updateEntity(Integer entity, Map<Integer, ImageView> entities){
        if(imageMap.containsKey(entity.getID())){
        	
            ImageView currentImage = imageMap.get(entity.getID());
            updateImage(currentImage, entity);
//            PathTransition pt = moveToLocation(currentImage, entity.getLocation());
//            trans.getChildren().add(pt);
//            root.getChildren().add(imageMap.get(entity.getID()));
        }
    }*/
   /* private void updateImage(ImageView currentImage, IRestrictedEntity re){
    	String[] image = re.getRestrictedImagePath().split("[\\\\/]");
        currentImage.setImage(new Image(getClass().getClassLoader().getResourceAsStream(image[image.length-1])));
        currentImage.setX(re.getRestrictedLocation().getWidth());
        currentImage.setY(re.getRestrictedLocation().getHeight());   
        //currentImage.setTranslateX(re.getLocation().getX()*50-475);
		//currentImage.setTranslateY(re.getLocation().getY()*50-175);
    }*/


    private FadeTransition makeFade(ImageView imageView){
        double newOpacity = 0.0;
        return createFade(newOpacity, imageView);
    }
    
    private FadeTransition makeAppear(ImageView imageView){
        double newOpacity = 1.0;
        return createFade(newOpacity, imageView);
    }
    
    private FadeTransition createFade(double newOpacity, ImageView imageView){
        FadeTransition ft = new FadeTransition(Duration.millis(KEY_INPUT_SPEED), imageView);
        ft.setToValue(newOpacity);
        return ft;
    }
    
    public void start(){
        animation.play();
    }
    
    public void pause(){
        animation.pause();
    }
    
    public void clearRoot(){
    	imageMap.clear();
    	root.getChildren().clear();
    }
    
}
