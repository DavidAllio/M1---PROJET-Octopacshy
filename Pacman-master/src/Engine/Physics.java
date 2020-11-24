package Engine;

import java.awt.*;
import java.util.ArrayList;

public class Physics {
	private final Core core;
	private double v=1;
	private double time=0;
	private final double player_max_speed =4.0;
	private final double player_acceleration =0.5;
	private int x_direction=0, y_direction=0;
	private boolean pause=false;
	private boolean player_defined =false;
	private Entity player;
	private Core.DIRECTION direction;

	private ArrayList<CollisionBox> walls = new ArrayList<>();
	private ArrayList<Entity> enemys = new ArrayList<>();
	private ArrayList<Entity> items = new ArrayList<>();
	private CollisionBox poulp_box;

	public void setSpeed(int s){
		this.v=s;
		time=0;
	}
	public void accelerate(){
		if(v> player_max_speed ||time>2)
			return;
		v+= player_acceleration * Math.exp(time);
		time+=0.1;
	}

	private void decelerate(){
		if (v > 0)
			v -= 0.2;
		else
			v=0;

		if(time>0)
			time-=0.1;
		else
			time=0;
	}

	public void move_player() {
		boolean collide=false;
		Point tmp = new Point((int) Math.round(v*x_direction),(int) Math.round(y_direction*v));
		if(pause || player == null){
			return;
		}
		player.translate(tmp.x,tmp.y);

		for(Entity enemy:enemys){
			if(poulp_box.is_collide(enemy.getCollisionBox(), tmp)) {
				core.getGame().enemy_collide_player(enemy, player);
				return;
			}
		}
		for(CollisionBox cb1 : walls) {
			if (poulp_box.is_collide(cb1, tmp)) {
				collide=core.getGame().player_collide_wall();
				break;
			}
		}

		if(!collide&&!poulp_box.is_border_collide(tmp)) {
			for(int i = 0; i< items.size(); i++){
				if(!items.get(i).is_dead()&&poulp_box.is_collide(items.get(i).getCollisionBox(),tmp)) {
					core.getGame().player_collide_item(items.get(i).getTag());
					items.get(i).kill();
					break;
				}
			}
		}else{
			decelerate();
			player.translate(-tmp.x, -tmp.y);
		}

		if(player==null)
			return;
		if(v>1.0){
			player.setDirection(direction);
		}else{
			player.setDirection(Core.DIRECTION.STOP);
		}
	}

	public void pause() {
		pause=true;
	}

	public void end_pause(){
		pause=false;
	}

	public Point get_player_direction(Entity enemy, boolean flee){
		int dx=0,dy=0;
		if(player.getCollisionBox().getX()>enemy.getCollisionBox().getX()) {
			if(flee)
				dx=-1;
			else
				dx=1;
		}
		if(player.getCollisionBox().getX()<enemy.getCollisionBox().getX()) {
			if(flee)
				dx=1;
			else
				dx=-1;
		}
		if(player.getCollisionBox().getY()>enemy.getCollisionBox().getY()) {
			if(flee)
				dy=-1;
			else
				dy=1;
		}
		if(player.getCollisionBox().getY()<enemy.getCollisionBox().getY()) {
			if(flee)
				dy=1;
			else
				dy=-1;
		}
		return new Point(dx,dy);
	}

	public void move_enemy() {

		if(pause || enemys==null|| player ==null){
			return;
		}
		core.getGame().ai_enemy(enemys,walls);

/*
		if(!collide&&!poulp_box.is_border_collide(tmp))
			poulp.translate(tmp.x,tmp.y);
		else
			poulp.getCollisionBox().translate(-tmp.x,-tmp.y);

		if(v>1.0){
			poulp.setDirection(direction);
		}else{
			poulp.setDirection(Core.DIRECTION.STOP);
		}

 */
	}

	Physics(Core core){
		this.core=core;
	}

	public void assign_player_vector(Entity entity , int x_direction, int y_direction, Core.DIRECTION wanted_direction) {
		this.x_direction=x_direction;
		this.y_direction=y_direction;
		this.direction=wanted_direction;

		if(!player_defined) {
			//On crée une boite de collision qui fait la moitiée de la taille du poulpe et qui est centrée sur celui-ci
			int x1,y1,w,h;
			x1=(int) (entity.getCoord().getX()+(0.25*entity.getSize().width));
			y1=(int) (entity.getCoord().getY()+(0.25*entity.getSize().height));
			w=entity.getSize().width/2;
			h=entity.getSize().height/2;
			poulp_box = new CollisionBox(x1,y1,w,h);
			entity.setCollisionBox(poulp_box);
			this.player = entity;
		}
		player_defined =true;
	}

	public void add_enemy_entity(Entity entity) {
		int x1,y1,w,h;

		x1=(int) (entity.getX()+(0.5*entity.getSize().width));
		y1=(int) (entity.getY()+(0.5*entity.getSize().height));

		w=entity.getSize().width-10;
		h=entity.getSize().height/2;
		CollisionBox enemy_box = new CollisionBox(x1,y1,w,h);
		entity.setCollisionBox(enemy_box);
		this.enemys.add(entity);

	}

	private void add_wall_hitbox(int i, int j, int y){
		if(j==-1)
			j=i;
		walls.add(new CollisionBox(i * Core.wall_size.width , y * Core.wall_size.height, (((j-i)*Core.wall_size.width)+Core.wall_size.width) , Core.wall_size.height));
	}

	public void construct_wall_hitboxs(ArrayList<String> actual_level) {
		int start=-1,end=-1;
		for(int i=0;i<actual_level.size();i++){
			loopj: for(int j=0;j<actual_level.get(i).length();j++){
				if(actual_level.get(i).charAt(j)=='1') {
					start = j;
					for (int k =j;k<actual_level.get(i).length();k++){
						if(actual_level.get(i).charAt(k)=='1'){
							end = k;
						}else{
							add_wall_hitbox(start,end,i);
							j=k;
							start = -1;
							end = -1;
							break;
						}
						if(k==actual_level.get(i).length()-1&&start!=-1) {
							add_wall_hitbox(start, end, i);
							break loopj;
						}
					}
				}

			}
		}
	}

	public void construct_items_hitbox(Entity ent) {
		ent.setCollisionBox(new CollisionBox((int) ent.getX() , (int) ent.getY(), Core.wall_size.width , Core.wall_size.height));
		items.add(ent);
	}

	public ArrayList<CollisionBox> getWalls() {
		return walls;
	}

	public ArrayList<Entity> getenemys() {
		return enemys;
	}

	public ArrayList<Entity> getitems() {
		return items;
	}

	public void remove_all_entity() {
		player =null;
		walls.removeAll(walls);
		enemys.removeAll(enemys);
		player_defined =false;
	}


}
