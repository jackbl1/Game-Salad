package gameView.commands;

import javafx.stage.Stage;
import gameView.ICommandView;
import gameView.tools.FrontEndException;
import gameView.tools.UserData;

public class RegisterCommand extends AbstractCommand {
	
	public RegisterCommand(ICommandView m) {
		super(m);
	}

	@Override
	public void execute(Stage s) {
	}
	
	public void execute(Stage s, String username, String password, String passwordCheck) {
		if (!password.equals(passwordCheck)) {
			throw new FrontEndException(String.format("PASSWORDS DO NOT MATCH: %s %s", password, passwordCheck));
		}
		//UserData user = new UserData(username, )
		//GET DATA FROM DATABASE
	}

	@Override
	public String getName() {
		return "Register";
	}

}
