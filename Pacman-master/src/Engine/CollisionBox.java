package Engine;

import java.awt.*;

public class CollisionBox{
	private int x;
	private int y;
	private boolean disabled=false;
	private final int width;
	private final int height;

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX(){
		return x;
	}

	CollisionBox(int x,int y, int width, int height){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}

    public void disable(){
		this.disabled=true;
    }
	public void enable(){
		this.disabled=false;
	}

	public boolean isDisabled(){
		return disabled;
	}

	public boolean is_collide(CollisionBox rect2, Point translation){
			return x+translation.x < rect2.x + rect2.width &&
				x+translation.x + width > rect2.x &&
				y+translation.y < rect2.y + rect2.height &&
				height + y+translation.y > rect2.y;
	}

	public boolean is_collide_right_left(CollisionBox cb, int x){
		return is_collide(cb,new Point(x,0));
	}

	public boolean is_collide_up_down(CollisionBox cb, int y){
		return is_collide(cb,new Point(0,y));
	}

	boolean is_collide_right(CollisionBox cb,int v){
		return is_collide(cb,new Point(v,0));
	}

	boolean is_collide_left(CollisionBox cb,int v){
		return is_collide(cb,new Point(-v,0));
	}

	boolean is_collide_up(CollisionBox cb,int v){
		return is_collide(cb,new Point(0,-v));
	}

	boolean is_collide_down(CollisionBox cb,int v){
		return is_collide(cb,new Point(0,v));
	}


	public boolean is_border_collide(Point translation) {
		return x+width+translation.x > 900 ||
				y+height+translation.y > 720 ||
				x + translation.x < 0 ||
				y + translation.y < 0;
	}

	public void translate(int x, int y) {
		this.x+=x;
		this.y+=y;
	}
}
