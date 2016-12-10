 

import java.util.Arrays;


public class GamesList {

	private Game[] game ;
	
	public GamesList(){
		super();
	}

	public Game[] getGame() {
		return game;
	}

	public void setGame(Game[] game) {
		this.game = game;
	}

	@Override
	public String toString() {
		return "GamesList [game=" + Arrays.toString(game) + "]";
	}
	
	
}
