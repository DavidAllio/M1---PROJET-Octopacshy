package Engine;

import java.awt.*;

public class Entity{
	private Core.DIRECTION direction;
	private Point coord;
	private Point original_coord;
	private CollisionBox collisionBox;
	private final Dimension size;
	private boolean killed = false;
	private String tag;
	private int delay;

	public Entity(Point coord, Dimension size, Core.DIRECTION direction){
		this(coord,size,direction, "None");
	}

	public Entity(Point coord, Dimension size, Core.DIRECTION direction, String tag){
		this.coord=coord;
		this.size=size;
		this.direction=direction;
		this.tag=tag;
		this.delay=-1;
		if(coord!=null)
			this.original_coord = new Point(coord.x,coord.y);
	}

	public void translate(int x, int y) {
		coord.setLocation(coord.x+x,coord.y+y);
		collisionBox.translate(x,y);
	}

	public void setCollisionBox(CollisionBox collisionBox){
		this.collisionBox=collisionBox;
	}

	public CollisionBox getCollisionBox(){
			return collisionBox;
	}

	public double getX(){
		return coord.getX();
	}

	public double getY(){
		return coord.getY();
	}
	public Core.DIRECTION getDirection(){
		return direction;
	}

	public Point getCoord(){
		return coord;
	}

	public void setCoord(Point p){
		if(coord==null) {
			coord = new Point(p.x, p.y);
			this.original_coord = new Point(coord.x,coord.y);
		}
		if(collisionBox!=null) {
			int dx = p.x - coord.x;
			int dy = p.y - coord.y;
			collisionBox.translate(dx, dy);
		}
		this.coord.x = p.x;
		this.coord.y = p.y;
	}

	public void resetCoord(){
		setCoord(getOriginal());
	}

	public void setDirection(Core.DIRECTION direction){
		this.direction=direction;
	}

	public Dimension getSize(){
		return size;
	}

	public void kill() {
		this.killed=true;
	}

	public boolean is_dead() {
		return killed;
	}

	public void respawn() {
		this.killed=false;
	}

	public String getTag() {
		return tag;
	}

	public Point getOriginal() {
		return original_coord;
	}

	public boolean isDelayActivaded(){ return (delay != -1);}

	public void countDelay(){
		if(!endedDelay())
			delay--;
	}

	public void initDelay(int delay){
		this.delay=delay;
	}

	public boolean endedDelay(){return delay==0;}

	public boolean notNullDelay(){return delay>0;}

	public boolean blink(){
		if(notNullDelay()){
			if((delay>=20&&delay<=40)||(delay>=60&&delay<=80)||(delay>=100&&delay<=120)||(delay>=140&&delay<=160)||(delay>=180&&delay<=200)||(delay>=220&&delay<=240)||(delay>=260&&delay<=280)||(delay>=300&&delay<=320)||(delay>=340&&delay<=360)||(delay>=380&&delay<=400)||(delay>=420&&delay<=440)||(delay>=460&&delay<=480)){
				return true;
			}
		}
		return false;
	}

	public int getDelay() {
		return delay;
	}
}

