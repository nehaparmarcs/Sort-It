 


public class Game {
	
	private String gameName;
	private String playerName;
	
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public Game(String gameName, String playerName) {
		super();
		this.gameName = gameName;
		this.playerName = playerName;
	}
	public Game() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Game [gameName=" + gameName + ", playerName=" + playerName + "]";
	}
	
	

}
