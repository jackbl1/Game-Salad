package gameEngine_interface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import actions.BlockBottomRegularCollision;
import actions.BlockTopRegularCollision;
import actions.BounceOffBlockBottomOrTop;
import actions.BounceOffBlockSide;
import actions.DoubleJump;
import actions.IAction;
import actions.ImageChangeAction;
import actions.PointsAction;
import actions.Reload;
import actions.RemoveAction;
import actions.ShootAction;
import actions.Teleport;
import alerts.VoogaError;
import components.entityComponents.AccelerationComponent;
import components.entityComponents.CheckCollisionComponent;
import components.entityComponents.CollidableComponent;
import components.entityComponents.CollisionComponentType;
import components.entityComponents.CollisionComponentsHandler;
import components.entityComponents.ComponentType;
import components.entityComponents.EntityType;
import components.entityComponents.GoalComponent;
import components.entityComponents.ImagePropertiesComponent;
import components.entityComponents.KeyInputComponent;
import components.entityComponents.LabelComponent;
import components.entityComponents.LocationComponent;
import components.entityComponents.ObjectCreationComponent;
import components.entityComponents.SideCollisionComponent;
import components.entityComponents.SpriteComponent;
import components.entityComponents.StepComponent;
import components.entityComponents.TerminalVelocityComponent;
import components.entityComponents.TimeComponent;
import components.entityComponents.TypeComponent;
import components.entityComponents.VelocityComponent;
import components.keyExpressions.JumpAction;
import components.keyExpressions.LeftAction;
import components.keyExpressions.RightAction;
import controller.Camera;
import controller.WorldAnimator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import data_interfaces.Communicator;
import data_interfaces.EngineCommunication;
import data_interfaces.XMLDefinedParser;
import engines.AIEngine;
import engines.AbstractEngine;
import engines.CollisionEngine;
import engines.InputEngine;
import engines.LevelEngine;
import engines.MovementEngine;
import engines.TimeEngine;
import entity.Entity;
import entity.EntityManager;
import entity.GPEntityManager;
import entity.IEntity;
import entity.restricted.IRestrictedEntity;
import entity.restricted.IRestrictedEntityManager;
import gameView.userInput.IRestrictedUserInputData;
import gamedata.GameData;
import gamedata.GameDataFactory;
import gamedata.IGameData;
import gamedata.IRestrictedGameData;
import engines.AbstractEngine;
import entity.IEntityManager;
import entity.SplashEntity;
import entity.presets.AbstractBlock;
import entity.presets.AbstractBreakableBox;
import entity.presets.AbstractEnemy;
import entity.presets.AbstractGoal;
import entity.presets.AbstractMysteryBlock;
import entity.presets.AbstractPowerup;
import gamedata.GameData;
/**
 * Basic GameEngine class Note: the engines must be created in someway, likely
 * via reflection
 * 
 * @author Bilva
 *
 */
public class GameEngine implements GameEngineInterface {
	private IEntityManager myEntityManager;// = new EntityManager(new ArrayList<Entity>()); 
	private List<AbstractEngine> myEngines;// = Arrays.asList(new NewMovementEngine(myEntityManager), new CollisionEngine(myEntityManager), new InputEngine(myEntityManager));
	private XMLDefinedParser myParser = new XMLDefinedParser();
	private Map<IEntity, IRestrictedEntity> entityToRestricted;
	private Entity mainCharacter;
	private GameData myGameData;
	private GPEntityManager GPEM;
	private double points=0;
	private double lives=0;
	private double level=0;
	private String music = "";
	private String currentMusic = "Obi-Wan - Hello there..wav";
	private Clip clip2;
	private Camera cam;

	private int numUpdates;
	private boolean sliderPause = false;
	private List<IEntityManager> myEntityManagers;
	private List<IEntityManager> previousEntityManagers;
	public static final int SAVE_FREQUENCY = WorldAnimator.FRAMES_PER_SECOND;
	
	public GameEngine(IRestrictedUserInputData data){
		ReadOnlyIntegerProperty p = data.getRewind();
		p.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	if (old_val.intValue()!=new_val.intValue()){
                    rewindState(new_val.intValue());
                    
            	}
                    System.out.println(new_val.intValue());
            }
        });
		previousEntityManagers = new ArrayList<IEntityManager>();

//		try{
//	    AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource(currentMusic));
//	    clip2 = AudioSystem.getClip();
//	    clip2.open(audioInputStream2);
//	    clip2.start();
//	}
//	catch(Exception ex)
//	{
//		new VoogaError("File Not Found", "Music Could Not Be Played");
//	}
	}
	
	public IRestrictedGameData loadData(EngineCommunication c){
//		Collection<IEntity> castedEnts = new ArrayList<IEntity>();
//		for (Entity e : c.getData()) {
//			castedEnts.add(e);
//		}
		
		myEntityManager = dummyLoad();//myEntityManagers.get(0);
		myEntityManagers = new ArrayList<IEntityManager>();
		myEntityManagers.add(myEntityManager);
//		GPEM = new GPEntityManager(c.getData());
//		myEngines = Arrays.asList(new MovementEngine(myEntityManager), new CollisionEngine(myEntityManager), new InputEngine(myEntityManager), new LevelEngine(myEntityManager));
		LocationComponent lc = (LocationComponent) getMainCharacter().getComponent(ComponentType.Location);
		myGameData = new GameData(points,lives,(IRestrictedEntityManager) myEntityManager, level, lc,currentMusic,"");
		IRestrictedGameData dg = (IRestrictedGameData) myGameData;
		return dg;
	}
	public Collection<IEntity> save(){
		return myEntityManager.copy().getEntities();
	}
	public SplashEntity getSplashEntity(){
		return GPEM.getSplash();
	}
	/**
	 * Runs each Engine in my Engine
	 */
	@Override
	public void handleUpdates(Collection<KeyCode> keysPressed) {
		if (sliderPause==true){
			sliderPause=false;
			previousEntityManagers.clear();
		}
		saveNewEntityManager();
		Collection <IEntity> changedEntity = new ArrayList<IEntity>();
		Map <Integer, IEntity> changedEntityMap = new HashMap<Integer,IEntity>();
		for (AbstractEngine s : myEngines){
			IGameData rgd = (IGameData) s.update(keysPressed,(IRestrictedGameData) myGameData);
			GameDataFactory gdf = new GameDataFactory();
			gdf.updateGameData(myGameData,rgd);		
		}
	}
	
	private void rewindState(Integer i){
		sliderPause=true;
		Integer size = previousEntityManagers.size();
		EntityLoader el = new EntityLoader(myEntityManager);
		Integer index=size-(11-i);
		if (index<0){
			index=0;
		}
		el.loadNew(previousEntityManagers.get(index));
		
	}
	private void saveNewEntityManager() {
		numUpdates++;
		if (numUpdates % SAVE_FREQUENCY*20 == 0) {
			previousEntityManagers.add((myEntityManager.copy()));
			
		}
		while (previousEntityManagers.size()>10) {
			previousEntityManagers.remove(0);
		}
//		if (numUpdates==200){
//			rewindState(0);
//			numUpdates=1;
//		}
	}
	
	
	
	//TODO: Dumb flappybird
	//	public GameData dummyLoad(){
	//		Collection<Entity> e = new ArrayList<Entity>();
	//		Entity x = new Entity(0);
	//		x.addComponent(new LocationComponent(100,150));
	//		x.addComponent(new SpriteComponent(("flappybird_yellow.png")));
	//		ImagePropertiesComponent xc = new ImagePropertiesComponent();
	//		x.addComponent(new CheckCollisionComponent(true));
	//		xc.setHeight(50);
	//		xc.setWidth(50);
	//		x.addComponent(xc);
	//		x.addComponent(new VelocityComponent(3,0));
	//		x.addComponent(new AccelerationComponent(0,0.1));
	//		x.addComponent(new CollidableComponent(true));
	//		x.addComponent(new LabelComponent("grrraah"));
	//		x.addComponent(new KeyInputComponent());
	//		x.addComponent(new TypeComponent(EntityType.Player));
	//		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, new DoubleJump());
	//		e.add(x);
	//		for (int i=1;i<10;i++){
	//			Entity p = new AbstractBlock(i);
	//			p.addComponent(new LocationComponent(i*200,200));
	//			p.addComponent(new SpriteComponent(("pipe_up.png")));
	//
	//			ImagePropertiesComponent xpc = new ImagePropertiesComponent();
	//			double d = Math.random();
	//			xpc.setHeight(100*d);
	//			xpc.setWidth(50);
	//			p.addComponent(xpc);
	//			p.addComponent(new LabelComponent("Blok"));
	//			p.addComponent(new TypeComponent(EntityType.Block));
	//			Entity q = new AbstractBlock(i*100);
	//			q.addComponent(new LocationComponent(i*200,00));
	//			q.addComponent(new SpriteComponent(("pipe_down.png")));
	//
	//			ImagePropertiesComponent xpq = new ImagePropertiesComponent();
	//			xpq.setHeight(100*(1-d));
	//			xpq.setWidth(50);
	//			q.addComponent(xpq);
	//			q.addComponent(new LabelComponent("Blok"));
	//			q.addComponent(new TypeComponent(EntityType.Block));
	//			
	//			
	//			e.add(p);
	//			e.add(q);
	//		}
	//		myEntityManager = new EntityManager(e);
	//
	//		//		myEngines = Arrays.asList(new NewMovementEngine(myEntityManager),new CollisionEngine(myEntityManager),new InputEngine(myEntityManager));
	//		myEngines = Arrays.asList(new InputEngine(myEntityManager), new NewMovementEngine(myEntityManager), new CollisionEngine(myEntityManager), new TimeEngine(myEntityManager));
	//		return new GameData(0,0, (IRestrictedEntityManager) myEntityManager, 0, (LocationComponent) getMainCharacter().getComponent(ComponentType.Location),"" );
	//	}
	
	private EntityManager loadFakeManager() {
		Collection<IEntity> e = new ArrayList<IEntity>();
		Collection<IEntity> e7 = new ArrayList<IEntity>();
		IEntity x = new Entity(0);
		x.addComponent(new LocationComponent(100,150));
		x.addComponent(new SpriteComponent(("mario_step2.gif")));
		ImagePropertiesComponent xc = new ImagePropertiesComponent();
		x.addComponent(new CheckCollisionComponent(true));
		xc.setHeight(50);
		xc.setWidth(50);
		x.addComponent(xc);
		x.addComponent(new VelocityComponent(0,0));
		x.addComponent(new AccelerationComponent(0,0));
		x.addComponent(new CollidableComponent(true));
		x.addComponent(new LabelComponent("grrraah"));
		x.addComponent(new KeyInputComponent());
		x.addComponent(new TypeComponent(EntityType.Player));

		List<String> collection = new ArrayList<String>();
		collection.add("mario_step1.gif");
		collection.add("mario_step2.gif");
		collection.add("mario_step3.gif");
		ImageChangeAction ica = new ImageChangeAction(collection);
		List<String> collection2 = new ArrayList<String>();
		collection2.add("mario_leftstep1.gif");
		collection2.add("mario_leftstep2.gif");
		collection2.add("mario_leftstep3.gif");
		ImageChangeAction ica2 = new ImageChangeAction(collection2);
		List<String> collection3 = new ArrayList<String>();
		collection3.add("mario_jump.gif");
		ImageChangeAction ica3 = new ImageChangeAction(collection3);

		x.addComponent(new GoalComponent());
		x.addComponent(new TerminalVelocityComponent(5,5));
		Entity y2 = new Entity(201);
		y2.addComponent(new LocationComponent(800,150));
		y2.addComponent(new SpriteComponent(("Feuer46.GIF")));
		ImagePropertiesComponent yc2 = new ImagePropertiesComponent();
		yc2.setHeight(50);
		yc2.setWidth(50);
		y2.addComponent(yc2);
		y2.addComponent(new VelocityComponent(3,0));
		y2.addComponent(new LabelComponent("grrraa"));
		y2.addComponent(new CollidableComponent(true));
		y2.addComponent(new TimeComponent(new RemoveAction(), 3000));
		y2.addComponent(new TypeComponent(EntityType.Projectile));
		y2.addComponent(new CheckCollisionComponent(true));
		x.addComponent(new ObjectCreationComponent(y2));
		TimeComponent time = new TimeComponent(new Reload(), 1000);
		x.addComponent(time);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.V, new ShootAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, new JumpAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, ica3);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new RightAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, ica);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new PointsAction(100));
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, new LeftAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, ica2);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.R, "if (vc.getY()==0) { vc.setY(-3) ; ac.setY(0.05) }");
		//
		//
		//		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.T, "REMOVE");
		e.add(x);
		return new EntityManager(e);
	}
	public IEntityManager dummyLoad(){
		Collection<Entity> e = new ArrayList<Entity>();
		Collection<Entity> e7 = new ArrayList<Entity>();
		Entity x = new Entity(0);
		x.addComponent(new LocationComponent(100,150));
		x.addComponent(new SpriteComponent(("mario_step2.gif")));
		ImagePropertiesComponent xc = new ImagePropertiesComponent();
		x.addComponent(new CheckCollisionComponent(true));
		xc.setHeight(50);
		xc.setWidth(50);
		x.addComponent(xc);
		x.addComponent(new VelocityComponent(0,0));
		x.addComponent(new AccelerationComponent(0,0));
		x.addComponent(new CollidableComponent(true));
		x.addComponent(new LabelComponent("grrraah"));
		x.addComponent(new KeyInputComponent());
		x.addComponent(new TypeComponent(EntityType.Player));

		List<String> collection = new ArrayList<String>();
		collection.add("mario_step1.gif");
		collection.add("mario_step2.gif");
		collection.add("mario_step3.gif");
		ImageChangeAction ica = new ImageChangeAction(collection);
		List<String> collection2 = new ArrayList<String>();
		collection2.add("mario_leftstep1.gif");
		collection2.add("mario_leftstep2.gif");
		collection2.add("mario_leftstep3.gif");
		ImageChangeAction ica2 = new ImageChangeAction(collection2);
		List<String> collection3 = new ArrayList<String>();
		collection3.add("mario_jump.gif");
		ImageChangeAction ica3 = new ImageChangeAction(collection3);

		x.addComponent(new GoalComponent());
		x.addComponent(new TerminalVelocityComponent(5,5));
		Entity y2 = new Entity(201);
		y2.addComponent(new LocationComponent(800,150));
		y2.addComponent(new SpriteComponent(("Feuer46.GIF")));
		ImagePropertiesComponent yc2 = new ImagePropertiesComponent();
		yc2.setHeight(50);
		yc2.setWidth(50);
		y2.addComponent(yc2);
		y2.addComponent(new VelocityComponent(3,0));
		y2.addComponent(new LabelComponent("grrraa"));
		y2.addComponent(new CollidableComponent(true));
		y2.addComponent(new TimeComponent(new RemoveAction(), 3000));
		y2.addComponent(new TypeComponent(EntityType.Projectile));
		y2.addComponent(new CheckCollisionComponent(true));
		x.addComponent(new ObjectCreationComponent(y2));
		TimeComponent time = new TimeComponent(new Reload(), 1000);
		x.addComponent(time);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.V, new ShootAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, new JumpAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, ica3);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new RightAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, ica);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new PointsAction(100));
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, new LeftAction());
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, ica2);
		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.R, "if (vc.getY()==0) { vc.setY(-3) ; ac.setY(0.05) }");
		//
		//
		//		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.T, "REMOVE");
		e.add(x);
		//		for (int i=0;i<20;i++){
		//			Entity x = new Entity(i);
		//			x.addComponent(new LocationComponent(i*50,450));
		//			x.addComponent(new SpriteComponent(("dirt.jpg")));
		//
		//			ImagePropertiesComponent xc = new ImagePropertiesComponent();
		//			xc.setHeight(50);
		//			xc.setWidth(50);
		//			x.addComponent(xc);
		//
		//			SideCollisionComponent scc = new SideCollisionComponent(CollisionComponentType.Top, new BlockTopRegularCollision());
		//			x.addComponent(scc);
		//
		//			x.addComponent(new LabelComponent("Block"));
		//			e.add(x);
		//		}
		//		e.add(g);e.add(t);
//		e.add(x);
		for (int i=1;i<35;i++){
			Entity p = new AbstractBlock(i);
			if (i!=12){
				p.addComponent(new LocationComponent(i*50,200));
			}else{
				p.addComponent(new LocationComponent(i*50,50));
			}
			p.addComponent(new SpriteComponent(("dirt.jpg")));
			ImagePropertiesComponent xpc = new ImagePropertiesComponent();
			xpc.setHeight(50);
			xpc.setWidth(50);
			p.addComponent(xpc);
			p.addComponent(new LabelComponent("Blok"));
			p.addComponent(new TypeComponent(EntityType.Block));
			p.addComponent(new CollidableComponent(true));
			e.add(p);
			e7.add(p);
		}
		Entity pr = new AbstractBreakableBox(2356);
		pr.addComponent(new LocationComponent(700,150));
		pr.addComponent(new SpriteComponent(("platform_tile_035.png")));
		ImagePropertiesComponent xpcr = new ImagePropertiesComponent();
		xpcr.setHeight(50);
		xpcr.setWidth(50);
		pr.addComponent(xpcr);
		pr.addComponent(new LabelComponent("Blok"));
		pr.addComponent(new TypeComponent(EntityType.Block));
		e.add(pr);
		Entity y = new AbstractPowerup(101);
		y.addComponent(new LocationComponent(1000,150));
		y.addComponent(new SpriteComponent(("platform_tile_057.png")));
		ImagePropertiesComponent yc = new ImagePropertiesComponent();
		yc.setHeight(50);
		yc.setWidth(50);
		y.addComponent(yc);
		y.addComponent(new VelocityComponent(0,0));
		y.addComponent(new LabelComponent("Blok"));
		//BLOCK
		y.addComponent(new TypeComponent(EntityType.Block));
		Entity p = new AbstractMysteryBlock(102,y); 
		p.addComponent(new LocationComponent(900,50));
		p.addComponent(new SpriteComponent(("platform_tile_023.png")));
		ImagePropertiesComponent xpc = new ImagePropertiesComponent();
		xpc.setHeight(50);
		xpc.setWidth(50);
		p.addComponent(xpc);
		p.addComponent(new LabelComponent("Blok"));
		e.add(p);
//		Entity goal = new AbstractGoal(243);
//		goal.addComponent(new LocationComponent(1300,150));
//		goal.addComponent(new SpriteComponent(("transparent.png")));
//		goal.addComponent(new ImagePropertiesComponent(50,50));
//		goal.addComponent(new LabelComponent("Goal"));
//		e.add(goal);
		for (int i= 0; i<2; i++){
			Entity enemy = new AbstractEnemy(106+i);
			if (i==0){
				enemy.addComponent(new LocationComponent(800, 20));
			}else{
				enemy.addComponent(new LocationComponent(1200,150));
			}
			enemy.addComponent(new SpriteComponent(("sand.jpg")));
			ImagePropertiesComponent goalc = new ImagePropertiesComponent();
			goalc.setHeight(50);
			enemy.addComponent(new StepComponent(50));
			enemy.addComponent(new VelocityComponent(-1,0));
			enemy.addComponent(new LabelComponent("wecamefromnothingtosomething"));
			goalc.setWidth(50);
			enemy.addComponent(new LabelComponent("Goal"));
			enemy.addComponent(goalc);
			e.add(enemy);
			enemy.addComponent(new CheckCollisionComponent(true));
		}
		p.addComponent(new TypeComponent(EntityType.Block));
		e.add(p);
		Entity portal2 = createPortal();
		e.add(portal2);
		e.add(createPortal2());
		Collection<IEntity> e1 = new ArrayList<IEntity>();
		for (Entity exp : e) {
			e1.add(exp);
		}
//		Entity u = new Entity(0);
//		u.addComponent(new LocationComponent(100,150));
//		u.addComponent(new SpriteComponent(("mario_step2.gif")));
//		ImagePropertiesComponent uc = new ImagePropertiesComponent();
//		u.addComponent(new CheckCollisionComponent(true));
//		uc.setHeight(50);
//		uc.setWidth(50);
//		u.addComponent(uc);
//		u.addComponent(new VelocityComponent(0,0));
//		u.addComponent(new AccelerationComponent(0,0));
//		u.addComponent(new CollidableComponent(true));
//		u.addComponent(new LabelComponent("grrraah"));
//		u.addComponent(new KeyInputComponent());
//		u.addComponent(new TypeComponent(EntityType.Player));
//
//		List<String> collectionu = new ArrayList<String>();
//		collectionu.add("mario_step1.gif");
//		collectionu.add("mario_step2.gif");
//		collectionu.add("mario_step3.gif");
//		ImageChangeAction icau = new ImageChangeAction(collectionu);
//		List<String> collection2u = new ArrayList<String>();
//		collection2u.add("mario_leftstep1.gif");
//		collection2u.add("mario_leftstep2.gif");
//		collection2u.add("mario_leftstep3.gif");
//		ImageChangeAction ica2u = new ImageChangeAction(collection2u);
//		List<String> collection3u = new ArrayList<String>();
//		collection3.add("mario_jump.gif");
//		ImageChangeAction ica3u = new ImageChangeAction(collection3u);
//
//		u.addComponent(new GoalComponent());
//		u.addComponent(new TerminalVelocityComponent(5,5));
//		Entity y2u = new Entity(201);
//		y2u.addComponent(new LocationComponent(800,150));
//		y2u.addComponent(new SpriteComponent(("Feuer46.GIF")));
//		ImagePropertiesComponent yc2u = new ImagePropertiesComponent();
//		yc2u.setHeight(50);
//		yc2u.setWidth(50);
//		y2u.addComponent(yc2u);
//		y2u.addComponent(new VelocityComponent(3,0));
//		y2u.addComponent(new LabelComponent("grrraa"));
//		y2u.addComponent(new CollidableComponent(true));
//		y2u.addComponent(new TimeComponent(new RemoveAction(), 3000));
//		y2u.addComponent(new TypeComponent(EntityType.Projectile));
//		y2u.addComponent(new CheckCollisionComponent(true));
//		u.addComponent(new ObjectCreationComponent(y2));
//		TimeComponent timeu = new TimeComponent(new Reload(), 1000);
//		u.addComponent(timeu);
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.V, new ShootAction());
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, new JumpAction());
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.W, ica3);
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new RightAction());
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, ica);
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.D, new PointsAction(100));
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, new LeftAction());
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.A, ica2);
//		((KeyInputComponent) u.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.R, "if (vc.getY()==0) { vc.setY(-3) ; ac.setY(0.05) }");
//		e7.add(u);
//		//
//		//
//		//		((KeyInputComponent) x.getComponent(ComponentType.KeyInput)).addToMap(KeyCode.T, "REMOVE");
//		Collection<IEntity> e8 = new ArrayList<IEntity>();
//		for (Entity exp : e7) {
//			e8.add(exp);
//		}
		myEntityManager = new EntityManager(e1);
//		myEntityManagers= new ArrayList<IEntityManager>();
//		myEntityManagers.add(myEntityManager);
//		myEntityManagers.add(new EntityManager(e8));
		myGameData= new GameData(0,0, (IRestrictedEntityManager) myEntityManager, 0, (LocationComponent) getMainCharacter().getComponent(ComponentType.Location), "", "" );

		myEngines = Arrays.asList(new InputEngine(myEntityManager), new MovementEngine(myEntityManager), new CollisionEngine(myEntityManager), new TimeEngine(myEntityManager),new AIEngine(myEntityManager));
		return myEntityManager;
//		return myGameData;
	}
	//for testing
	public void addCamera(Camera c) {
		myEntityManager.changed(c);
	}
	private Entity createPortal() {
		Entity portal2 = new Entity(110);
		portal2.addComponent(new LocationComponent(450, 125));
		portal2.addComponent(new SpriteComponent("platform_tile_063.png"));
		SideCollisionComponent scc = new SideCollisionComponent(CollisionComponentType.Top);
		scc.addActionForLabel(new LabelComponent("grrraah"), new Teleport(100, 100));
		CollisionComponentsHandler cch = new CollisionComponentsHandler();
		cch.addCollisionComponent(scc);
		portal2.addComponent(cch);
		portal2.addComponent(new TypeComponent(EntityType.Block));
		ImagePropertiesComponent ipc = new ImagePropertiesComponent();
		ipc.setHeight(50);
		ipc.setWidth(50);
		portal2.addComponent(ipc);
		portal2.addComponent(new CollidableComponent(true));
		return portal2;
	}
	private Entity createPortal2() {
		Entity portal2 = new Entity(112);
		portal2.addComponent(new LocationComponent(1500, 125));
		portal2.addComponent(new SpriteComponent("portal.png"));
		SideCollisionComponent scc = new SideCollisionComponent(CollisionComponentType.Right);
		scc.addActionForLabel(new LabelComponent("grrraah"), new Teleport(100, 100));
		CollisionComponentsHandler cch = new CollisionComponentsHandler();
		cch.addCollisionComponent(scc);
		portal2.addComponent(cch);
		portal2.addComponent(new TypeComponent(EntityType.Block));
		ImagePropertiesComponent ipc = new ImagePropertiesComponent();
		ipc.setHeight(50);
		ipc.setWidth(50);
		portal2.addComponent(ipc);
		portal2.addComponent(new CollidableComponent(true));
		return portal2;
	}
	public IEntity getMainCharacter(){
		for(IEntity e : myEntityManager.getEntities()){
			if(e.getComponent(ComponentType.KeyInput) != null){
				return e;
			}
		}
		return null;
	}
}