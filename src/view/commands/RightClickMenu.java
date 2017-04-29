package view.commands;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import view.UtilityFactory;
import view.ViewData;

public class RightClickMenu{
	private boolean isShowing;
	private ViewData myData;
	private UtilityFactory utilF;
	private ContextMenu currentMenu;
	
	public RightClickMenu(UtilityFactory utilIn, ViewData dataIn, double x, double y){
		utilF = utilIn;
		myData = dataIn;
		isShowing = false;
	}
	
	private void fillMenu(List<MenuItem> menuItems, ContextMenu menu) {
		menuItems.stream().forEach(menu.getItems()::add);
	}
	
	public void show(Pane pane, double x, double y, double placex, double placey){
		ContextMenu newMenu = new ContextMenu();
		fillMenu(utilF.makeRightClickMenu(myData, placex, placey), newMenu);
		currentMenu = newMenu;
		currentMenu.show(pane, x, y);
		isShowing = true;
	}
	
	public void hide(){
		currentMenu.hide();
		isShowing = false;
	}
	
	public boolean isShowing(){
		return isShowing;
	}

}