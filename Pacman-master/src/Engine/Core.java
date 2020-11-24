package Engine;

import Game.Game;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Core {

	private Game game;

	public enum DIRECTION {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		STOP
	}

	public static Dimension GameSize = new Dimension(900,720);

	public static Dimension wall_size = new Dimension(64,59);
	public static Dimension player_size = new Dimension(79,75);
	public static Dimension enemy_size = new Dimension(120,90);

	private final ArrayList<String> actual_level = new ArrayList<>();

	private final Graphics game_graphics;
	private final Physics game_physics;

	private Entity poulp;
	private ArrayList<Entity> swordfish = new ArrayList<>();
	private ArrayList<Entity> items = new ArrayList<>();

	private int nbr_enemy, nbr_fish;

	public void init_poulp(Entity entity) {
		poulp=entity;
	}

	public Entity getPoulp(){
		return poulp;
	}

	public void addEnemy(Entity entity){
		swordfish.add(entity);
		nbr_enemy++;
	}

	public int getNbr_enemy() {
		return nbr_enemy;
	}

	public int getNbr_fish(){
		return nbr_fish;
	}
	public ArrayList<Entity> getSwordfish(){
		return swordfish;
	}

	public void deleteEnemys(){
		swordfish.removeAll(swordfish);
		nbr_enemy=0;
	}
	public void addItems(Entity ent){
		items.add(ent);
		if(ent.getTag()=="fish")
			nbr_fish++;
	}
	public ArrayList<Entity> getItems(){
		return items;
	}
	public void deleteItems(){
		items.removeAll(items);
		nbr_fish =0;
	}

	public Core(AnchorPane GameP){
		game_physics = new Physics(this);
		game_graphics = new Graphics(GameP, this);

	}

	public Game getGame() {
		return game;
	}
	public void setGame(Game game){
		this.game = game;
	}
	public Graphics getGame_graphics() {
		return game_graphics;
	}

	public Physics getGame_physics(){
		return game_physics;
	}

	public void move_poulp(Core.DIRECTION direction){
		int x_dir=0,y_dir=0;

		switch (direction){
			case UP:
				x_dir=0;y_dir=-1;
				break;
			case DOWN:
				x_dir=0;y_dir=1;
				break;
			case RIGHT:
				x_dir=1;y_dir=0;
				break;
			case LEFT:
				x_dir=-1;y_dir=0;
				break;
			case STOP:
				x_dir=0;y_dir=0;
				break;
			default:
				break;
		}

		move_poulp(x_dir,y_dir,direction);


	}
	private void move_poulp(int x_direction, int y_direction, Core.DIRECTION direction){
		game_physics.assign_player_vector(poulp,x_direction, y_direction, direction);
		game_physics.accelerate();

	}


	public int load_level_from_file(String filename){
		File file = new File(filename);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		while (scanner.hasNextLine()) {
			actual_level.add(scanner.nextLine());
		}
		scanner.close();
		game_graphics.dispose_level(actual_level);
		constructLevelPhysic();
		return nbr_fish;
	}

	public int load_level(String filename){
		Scanner scanner = new Scanner(Interface.class.getClassLoader().getResourceAsStream("levels/"+filename));
		while (scanner.hasNextLine()) {
			actual_level.add(scanner.nextLine());
		}
		scanner.close();
		game_graphics.dispose_level(actual_level);
		constructLevelPhysic();
		return nbr_fish;
		//game_graphics.drawcollisions(game_physics.getWalls());
	}

	private void constructLevelPhysic() {
		for(Entity ent:swordfish){
			game_physics.add_enemy_entity(ent);
		}
		for(Entity ent: items){
			game_physics.construct_items_hitbox(ent);
		}
		game_physics.construct_wall_hitboxs(actual_level);
	}

	public void reset_level() {
		dispose_level();
	}

	public void dispose_level(){
		poulp.respawn();
		poulp.resetCoord();

		for(Entity enemy:getSwordfish()){
			enemy.resetCoord();
		}
		for(Entity fish:getItems()){
			fish.respawn();
		}
	}

	public void delete_level(){
		actual_level.removeAll(actual_level);
		game_physics.remove_all_entity();
		game_graphics.remove_all_entity();
		game.reset_power_up();

	}

	public int count_killed_enemy(){
		int count=0;
		for(Entity ent:swordfish){
			if(ent.is_dead())
				count++;
		}
		return count;
	}

	public ArrayList<CollisionBox> getWalls(){
		return game_physics.getWalls();
	}


}
