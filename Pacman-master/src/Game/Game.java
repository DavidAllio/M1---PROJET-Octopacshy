package Game;

import Engine.*;
import javafx.application.Platform;

import java.awt.*;
import java.util.ArrayList;


public class Game {
	private final Core core;
	private final int number_level=3;
	private Engine.Graphics graphics;
	private Engine.Physics physics;
	private int eaten_fish=0, score=0, total_score=0, life=3,power_up_time=500, actual_level=0;
	private boolean wall_enabled=true,power_up=false,program_start=true;

	public Game(Core core){
		this.core=core;
		graphics = core.getGame_graphics();
		physics = core.getGame_physics();
		core.setGame(this);
	}

	/**
	 Execute les premières commandes de gameplay à l'appel de la class
	 */
	public void start(){
		next_level();
	}

	/**
	 Appelle le mouvement du personnage correspondant à la touche en paramètre
	 */
	public void keyPressed(String key_value){
		if(isProgram_start()){
			if(key_value.equals("o"))
				program_started();
			return;
		}
		if(core.getPoulp().is_dead()){
			if (key_value.equals("r")) {
				if (life > 0) {
					core.reset_level();
				} else {
					core.delete_level();
					core.load_level("level0.txt");
					life = 3;
					total_score=0;
				}
				score=0;
				physics.end_pause();
				return;
			}
		}
		switch(key_value){
			case "z":
			case "Z":
				core.move_poulp(Core.DIRECTION.UP);
				break;
			case "s":
			case "S":
				core.move_poulp(Core.DIRECTION.DOWN);
				break;
			case "d":
			case "D":
				core.move_poulp(Core.DIRECTION.RIGHT);
				break;
			case "q":
			case "Q":
				core.move_poulp(Core.DIRECTION.LEFT);
				break;
			default:
		}
	}

	public int getPower_up_time(){
		return power_up_time;
	}

	public boolean cross_walls_power(){
		return !wall_enabled;
	}

	/**
	 *On calcule des choses, entre autre le temps du restant du power up, le délai de clignotage des ennemis...
	 */
	public void calculator() {
		boolean stop=false;
		power_up_time--;
		for(Entity ent:core.getSwordfish()){
			ent.countDelay();
			if(power_up_time==150) {
				ent.initDelay(150);
				ent.kill();
			}
			if(ent.endedDelay()||power_up_time==0) {
				//ent.getCollisionBox().enable();
				ent.respawn();
			}
			if(power_up_time==0) {
				reset_power_up();
			}
		}

	}

	public void enemy_collide_player(Entity enemy,Entity player){
		physics.pause();
		if(power_up){

			if(enemy.getCollisionBox().isDisabled()) {
				physics.end_pause();
				return;
			}

			if(!enemy.is_dead()){
				score += total_score+100;
				enemy.initDelay(500);

				enemy.kill();
				enemy.getCollisionBox().disable();
				enemy.resetCoord();

				if(core.count_killed_enemy()==2)
					wall_enabled=false;
				physics.end_pause();
				return;
			}else{
				if(enemy.endedDelay()){
					game_over(player);
					physics.end_pause();
					return;
				}
			}

			if(enemy.notNullDelay()) {
				physics.end_pause();
				return;
			}
			physics.end_pause();
		}else {
			if(!player.is_dead())
				game_over(player);
		}
	}

	private void game_over(Entity player) {
		player.kill();
		reset_power_up();
		eaten_fish = 0;
		life -= 1;
		if(life==0){
			draw_big_text(0, 100, "\t      Perdu !\n\n  Appuyez sur la touche\n\t\t  \"r\"\n    pour recommencer\n    du premier niveau",0.749,0.286,0.286);
		}else {
			draw_big_text(0, 100, "\t      Touché !\n\n  Appuyez sur la touche\n\t\t  \"r\"\n    pour recommencer",0.0,0.462,0.87);
		}
	}

	private void draw_big_text(int x, int y, String s, double r, double g, double b) {
		Platform.runLater(() ->graphics.draw_big_text(x,y,s,r,g,b));
	}

	public void next_level(){
		if(actual_level>number_level-1){
			core.reset_level();
			return;
		}

		core.delete_level();
		core.load_level("level"+actual_level+".txt");
		actual_level++;
		return;
	}

	public void player_collide_item(String item_tag){
		if(item_tag.equals("fish")){
			eaten_fish++;
			if(eaten_fish==core.getNbr_fish()) {
				power_up=false;
				next_level();
				eaten_fish=0;
				total_score += score;
				reset_power_up();
			}
			score = total_score + (eaten_fish * 10);
		}
		if(item_tag.equals("powerup")){
			//wall_enabled=false;
			if(power_up)
				power_up_time+=500;
			else
				power_up=true;
		}

	}

	public boolean player_collide_wall() {
		return wall_enabled;
	}

	public int getScore() {
		return score;
	}

	public int getLife() {
		return life;
	}

	public boolean isProgram_start(){
		return program_start;
	}

	public void program_started(){
		program_start=false;
	}

	public boolean is_powerup_activaded() {
		return power_up;
	}

	public void ai_enemy(ArrayList<Entity> enemys, ArrayList<CollisionBox> walls) {
		boolean x_collide = false, y_collide = false;

		for (Entity enemy : enemys) {
			Point tmp = core.getGame_physics().get_player_direction(enemy,power_up&&enemy.isDelayActivaded());

			for (CollisionBox cb1 : walls) {

				if (enemy.getCollisionBox().is_collide_right_left(cb1, tmp.x)) {
					x_collide = true;
				}
				if (enemy.getCollisionBox().is_collide_up_down(cb1, tmp.y)) {
					y_collide = true;
				}
				if (x_collide && y_collide)
					break;
			}
			for (Entity other_enemy : enemys) {
				if (other_enemy != enemy) {
					CollisionBox cb1 = other_enemy.getCollisionBox();
					if (enemy.getCollisionBox().is_collide_right_left(cb1, tmp.x)) {
						x_collide = true;
						break;
					}
					if (enemy.getCollisionBox().is_collide_up_down(cb1, tmp.y)) {
						y_collide = true;
						break;
					}
				}
			}

			if (!x_collide && !enemy.getCollisionBox().is_border_collide(new Point(tmp.x, 0))) {
				enemy.translate(tmp.x, 0);
			}
			if (!y_collide && !enemy.getCollisionBox().is_border_collide(new Point(0, tmp.y))) {
				enemy.translate(0, tmp.y);
			}
			x_collide = false;
			y_collide = false;
		}
	}


	public void reset_power_up() {
		for(Entity ent:core.getSwordfish()){
				ent.getCollisionBox().enable();
				ent.respawn();
				ent.initDelay(-1);
		}
		power_up_time=500;
		power_up=false;
		if(!wall_enabled) {
			for(CollisionBox cb: core.getWalls()){
				if(core.getPoulp().getCollisionBox().is_collide(cb,new Point(0,0))){
					core.getPoulp().resetCoord();
					break;
				}
			}

		}
		wall_enabled=true;
	}
}
