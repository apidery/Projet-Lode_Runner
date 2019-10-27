package decorators;

import java.util.List;
import java.util.Map;

import data.Command;
import data.Direction;
import data.Item;
import data.Status;
import impl.LodeRunnerFrame;
import services.Attack;
import services.Board;
import services.EditableScreen;
import services.Engine;
import services.Environnement;
import services.GameObject;
import services.Guard;
import services.PathResolver;
import services.Player;

public class EngineDecorator implements Engine{

	private Engine delegate;
	
	public EngineDecorator(Engine delegate) {
		this.delegate = delegate;
	}

	public Environnement getCurrentEnvironnement() {
		return delegate.getCurrentEnvironnement();
	}

	public Player getPlayer() {
		return delegate.getPlayer();
	}
	
	@Override
	public PathResolver getCurrentPathResolver() {
		return delegate.getCurrentPathResolver();
	}

	public List<Guard> getGuards() {
		return delegate.getGuards();
	}

	public List<Item> getTreasures() {
		return delegate.getTreasures();
	}

	public Command getNextCommand() {
		return delegate.getNextCommand();
	}

	public Status getStatus() {
		return delegate.getStatus();
	}

	public Map<GameObject, Integer> getHoles() {
		return delegate.getHoles();
	}

	public void addHole(int x, int y) {
		delegate.addHole(x, y);
	}

	public void step() {
		delegate.step();
	}

	@Override
	public void init(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		delegate.init(wdtScreen, hgtScreen, playerX, playerY);
	}
	
	@Override
	public void initWithContract(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		delegate.initWithContract(wdtScreen, hgtScreen, playerX, playerY);
	}

	@Override
	public void initForTests(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		delegate.initForTests(wdtScreen, hgtScreen, playerX, playerY);
	}
	
	@Override
	public void setNextCommand(Command command) {
		delegate.setNextCommand(command);
	}

	@Override
	public Board getBoard() {
		return delegate.getBoard();
	}

	@Override
	public LodeRunnerFrame getFrame() {
		return delegate.getFrame();
	}

	@Override
	public int getScore() {
		return delegate.getScore();
	}

	@Override
	public int getNbTreasuresTotal() {
		return delegate.getNbTreasuresTotal();
	}

	@Override
	public int getNbStep() {
		return delegate.getNbStep();
	}

	@Override
	public void createEnvironnements(int wdt, int hgt) {
		delegate.createEnvironnements(wdt, hgt);
	}

	@Override
	public void createEnvironnementsWithContract(int wdt, int hgt) {
		delegate.createEnvironnementsWithContract(wdt, hgt);
	}

	@Override
	public void nextLevel() {
		delegate.nextLevel();
	}

	@Override
	public Map<Integer, Environnement> getLevels() {
		return delegate.getLevels();
	}

	@Override
	public int getCurrentLevel() {
		return delegate.getCurrentLevel();
	}

	@Override
	public int getCurrentNbTreasures() {
		return delegate.getCurrentNbTreasures();
	}

	@Override
	public int getScreenWdt() {
		return delegate.getScreenWdt();
	}

	@Override
	public int getScreenHgt() {
		return delegate.getScreenHgt();
	}

	@Override
	public List<Attack> getAttacks() {
		return delegate.getAttacks();
	}

	@Override
	public void addAttack(int x, int y, Direction direction) {
		delegate.addAttack(x, y, direction);
	}

	@Override
	public Map<Integer, Boolean> getPlayableLevels() {
		return delegate.getPlayableLevels();
	}

	@Override
	public Map<Integer, PathResolver> getPathResolvers() {
		return delegate.getPathResolvers();
	}

	@Override
	public boolean hasAttack(int wdt, int hgt, Direction direction) {
		return delegate.hasAttack(wdt, hgt, direction);
	}

	@Override
	public Map<Integer, EditableScreen> getEditors() {
		return delegate.getEditors();
	}
}
