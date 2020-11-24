package Engine;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class Graphics {
	public Core core;
	public static boolean debug_mode=false;

	private final Canvas BackgroundCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);
	private final Canvas PoulpCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);
	private final Canvas EnnemyCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);
	private final Canvas DebugCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);
	private final Canvas ItemsCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);
	private final Canvas HUDCanvas = new Canvas(Core.GameSize.width,Core.GameSize.height);

	private final GraphicsContext poulp_gc;
	private final GraphicsContext items_gc;
	private final GraphicsContext ennemy_gc;
	private final GraphicsContext back_gc;
	private final GraphicsContext debug_gc;
	private final GraphicsContext hud_gc;

	private ImageView imageView = new ImageView();

	private ArrayList<Image> poulp_pic = new ArrayList<>();
	private Image previous_picture, angry_poulp;

	private ArrayList<Image> swordfish_pic = new ArrayList<>();
	private Image scared_swordfish;

	private Image rock;
	private Image start_screen;
	private Image starfish_pic;

	private ArrayList<Image> fishs_pic= new ArrayList<>();

	private int imageIndex = 0 ;
	private int imageIndex2 = 0 ;
	private int imageIndex3 = 0 ;
	private final int frameTime = 20;
	private double pos=-385.0;
	private boolean left=true;

	/**
	 Initialise la classe Graphics et la connecte à la zone de dessin
	 @param GameP Le Panel ou sera fait l'affichage
	 */
	public Graphics(AnchorPane GameP, Core core){
		this.core=core;

		init_player();
		poulp_gc = PoulpCanvas.getGraphicsContext2D();
		back_gc = BackgroundCanvas.getGraphicsContext2D();
		ennemy_gc = EnnemyCanvas.getGraphicsContext2D();
		debug_gc = DebugCanvas.getGraphicsContext2D();
		items_gc = ItemsCanvas.getGraphicsContext2D();
		hud_gc = HUDCanvas.getGraphicsContext2D();

		load_ressources();

		Core.enemy_size = new Dimension(120,90);
		Core.player_size = new Dimension(79,75);
		Core.wall_size = new Dimension(64,59);
		animations_load();
		layers_setting_up(GameP);
	}

	public void init_player(){
		core.init_poulp(new Entity(null,Core.player_size, Core.DIRECTION.DOWN));
	}

	/**
	 Effectue la rotation d'une image avec l'angle spécifié.
	 @param img L'image auquel appliquer une rotation
	 @param angle Angle de la rotation en degré
	 @return L'image avec la bonne rotation
	 */
	private Image rotate_image(Image img, double angle){
		//Conversion image -> bufferedImage
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img,null);

		//rotation
		AffineTransform tx = new AffineTransform();
		tx.rotate(Math.toRadians(angle), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_BILINEAR);
		bufferedImage = op.filter(bufferedImage, null);

		return SwingFXUtils.toFXImage(bufferedImage,null);
	}

	/**
	 Effectuer une symétrie verticale d'une image
	 @param img L'image sur laquelle appliquer une symétrie
	 @return L'image avec la symétrie effectuée'
	 */
	private Image flip_image(Image img){
		Image image=img;
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(),0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage bimg = op.filter(SwingFXUtils.fromFXImage(img,null), null);
		image=SwingFXUtils.toFXImage(bimg,null);
		return image;

	}

	/**
	 Effectuer le redimensionnement d'une image en divisant sa taille par 2
	 @param img L'image à redimensionner
	 @return L'image redimensionnée
	 */
	private Image resize_by_half(Image img){
		BufferedImage dbi = null;
		BufferedImage sbi = SwingFXUtils.fromFXImage(img,null);
		if(sbi != null) {
			dbi = new BufferedImage((int) img.getWidth()/2, (int) img.getHeight()/2, sbi.getType());
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(0.5, 0.5);
			g.drawRenderedImage(sbi, at);
		}
		return SwingFXUtils.toFXImage(dbi,null);
	}


	/**
	 Charger une image à partir des ressources
	 @param file_directory Chemin de l'image dans les ressources
	 @return L'image désirée
	 */
	private Image load_picture(String file_directory){
		Image image;
		try(InputStream in = Interface.class.getClassLoader().getResourceAsStream(file_directory)) {
			image = new Image(in);
		} catch (IOException e) {
			image=null;
			e.printStackTrace();
		}
		return image;
	}

	private ArrayList<Image> load_pictures(String file_directory, int nbr_image){
		return load_pictures(file_directory, nbr_image, 1);
	}

	private ArrayList<Image> load_pictures(String file_directory, int nbr_image, int increment_between_files){
		ArrayList<Image> imgs = new ArrayList<>();
		int incr=1;
		String actualn;
		for (int i = 1; i <= nbr_image; i++) {
			actualn = String.format("%04d", incr);
			try(InputStream in = Interface.class.getClassLoader().getResourceAsStream(file_directory +"/"+actualn+".png")) {
				Image image = new Image(Objects.requireNonNull(in));
				imgs.add(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
			incr+=increment_between_files;

		}
		return  imgs;
	}

	/**
	 Lire les ressources graphiques du jeu à partir des fichiers et les enregistrer en mémoire
	 */
	private void load_ressources(){


			poulp_pic = load_pictures("graphical/poulp",30,4);
			swordfish_pic = load_pictures("graphical/swordfish",80);
			fishs_pic = load_pictures("graphical/fishs",3);
			start_screen = load_picture("graphical/background/accueil.png");
			angry_poulp = load_picture("graphical/poulp/demo.png");
			scared_swordfish = load_picture("graphical/swordfish/demo.png");
			rock = load_picture("graphical/wall/rock.png");
			starfish_pic = load_picture("graphical/starfish/starfish.png");
			imageView.setImage(load_picture("graphical/background/source.gif"));

	}

	/**
	 Mettre en place les différents calques de dessin dans le Panel
	 @param GameP Panel ou seront insérés les calques
	 */
	private void layers_setting_up(AnchorPane GameP) {

		imageView.setFitHeight(Core.GameSize.height);
		imageView.setFitWidth(Core.GameSize.width);

		GameP.getChildren().add(HUDCanvas);
		GameP.getChildren().add(PoulpCanvas);
		GameP.getChildren().add(EnnemyCanvas);
		GameP.getChildren().add(ItemsCanvas);
		GameP.getChildren().add(DebugCanvas);
		GameP.getChildren().add(BackgroundCanvas);

		PoulpCanvas.toFront();
		if(!debug_mode) {
			GameP.getChildren().add(imageView);
			imageView.toFront();
			imageView.setOpacity(0.4);
			//DebugCanvas.setVisible(false);
		}
		HUDCanvas.toFront();
		ItemsCanvas.toBack();
		BackgroundCanvas.toBack();
	}

	private Timeline assign_animation_to_timeline(KeyFrame keyframe){
		Timeline timeline;
		timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);

		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		return timeline;
	}

	/**
	 Initialiser les timeline avec les keyframe du poulpe et celles de l'espadon
	 */
	private void animations_load() {
		assign_animation_to_timeline(poulp_animation_load());
		assign_animation_to_timeline(swordfish_animation_load());
		assign_animation_to_timeline(fish_animation_load());
	}

	private KeyFrame fish_animation_load() {
		return new KeyFrame(Duration.millis(500),
				event -> {
					if(core.getPoulp().is_dead())
						return;

					int fish_animation_index = (imageIndex3++) % 3;

					items_gc.clearRect(0, 0, 900, 720);
					//fish_gc.drawImage(rock,fishs.get(0).getX() * Core.wall_size.x, fishs.get(0).getY() * Core.wall_size.y);
					for(Entity fish:core.getItems()) {
						if (!fish.is_dead()) {
							if(fish.getTag().equals("fish")) {
								items_gc.drawImage(rotate_image(fishs_pic.get(fish_animation_index), Math.random() * 360), fish.getX(), fish.getY());
							}
							if(fish.getTag().equals("powerup")) {
								items_gc.drawImage(starfish_pic, fish.getX(), fish.getY());
							}
						}
					}
				});
	}

	private void draw_hud(int bonus_time) {

		if(core.getGame().isProgram_start()) {
			return;
		}

		String actual_score, actual_life="";
		int life=core.getGame().getLife();

		for(int i=1;i<=life;i++){
			actual_life += "♥";
		}
		actual_score = String.format("%04d", core.getGame().getScore());
		hud_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height+50);
		hud_gc.setStroke(Color.BLACK);
		hud_gc.setFill(Color.WHITE);
		hud_gc.setFont(javafx.scene.text.Font.font("Arial",FontWeight.EXTRA_BOLD, 32));
		if(bonus_time!=500)
			hud_gc.fillText("Score : "+actual_score+"\tVie : "+actual_life+"\t\tPower up : "+bonus_time/50, 20, Core.GameSize.height-5);
		else
			hud_gc.fillText("Score : "+actual_score+"\tVie : "+actual_life, 20, Core.GameSize.height-5);
	}

	public void show_start_screen() {
		//hud_gc.setFill(Color.color(0.5,0.5,0));
		//hud_gc.fillRect(0, 0, Core.GameSize.width, Core.GameSize.height+70);
		hud_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height+50);
		hud_gc.drawImage(start_screen, pos,0);
		hud_gc.setFill(Color.WHITE);
		hud_gc.setFont(javafx.scene.text.Font.font("Arial",FontWeight.EXTRA_BOLD, 32));

		hud_gc.fillText("Appuyez sur \"o\" pour jouer", (Core.GameSize.width/2)-250, Core.GameSize.height-20);

		if(left&&pos>-385)
			pos-=0.4;
		else {
			left = false;
			if(!left&&pos<-150)
				pos+=0.4;
			else
				left=true;
		}

	}

	/**
	 Définir dans des keyframe les successions d'images qui créeront les animations des espadons.
	 */
	private KeyFrame swordfish_animation_load() {
		return new KeyFrame(Duration.millis(frameTime),
				event -> {
					int sfish_animation_index = (imageIndex2++) % swordfish_pic.size();

					ennemy_gc.clearRect(0, 0, Core.GameSize.width, Core.GameSize.height);
					for(Entity enemy:core.getSwordfish()) {
						player_looked_by(enemy);

						if(core.getGame().is_powerup_activaded()) {
							if(!enemy.is_dead()) {
								// Si le power up est activé et qu'on est à la fin de son temps d'activation, on fait clignoter l'ennemi
								if(core.getGame().getPower_up_time()<=100){
									if(enemy.blink()) {
										if (enemy.getDirection() == Core.DIRECTION.RIGHT) {
											ennemy_gc.drawImage(flip_image(scared_swordfish), enemy.getX(), enemy.getY());
										} else {
											ennemy_gc.drawImage(scared_swordfish, enemy.getX(), enemy.getY());
										}
									}

								}else {
									// Si le power up est activé et que l'ennemi n'a pas été attrapé par le poulpe, on affiche l'enemi avec l'apparence cartoon
									if (enemy.getDirection() == Core.DIRECTION.LEFT) {
										ennemy_gc.drawImage(flip_image(scared_swordfish), enemy.getX(), enemy.getY());
									} else {
										ennemy_gc.drawImage(scared_swordfish, enemy.getX(), enemy.getY());
									}
								}
							}else{
								//Si l'ennemi a été attrapé par le joueur, on le fait clignoter avec son apparence de base
								if(!enemy.blink()) {
									if (enemy.getDirection() == Core.DIRECTION.RIGHT) {
										ennemy_gc.drawImage(flip_image(swordfish_pic.get(sfish_animation_index)), enemy.getX(), enemy.getY());
									} else {
										ennemy_gc.drawImage(swordfish_pic.get(sfish_animation_index), enemy.getX(), enemy.getY());
									}
								}

							}
						}else {
							//Si le power up n'est pas activé, on laisse l'apparence de base de l'ennemi et on l'oriente en fonction de la position du joueur
							if (enemy.getDirection() == Core.DIRECTION.RIGHT) {
								ennemy_gc.drawImage(flip_image(swordfish_pic.get(sfish_animation_index)), enemy.getX(), enemy.getY());
							} else {
								ennemy_gc.drawImage(swordfish_pic.get(sfish_animation_index), enemy.getX(), enemy.getY());
							}
						}
					}
				});
	}

	/**
	 Oriente graphiquement les enemis par rapport au joueur
	 @param enemy L'enemy à orienter
	 */
	private void player_looked_by(Entity enemy) {
		if(core.getPoulp().getX()>enemy.getX())
			enemy.setDirection(Core.DIRECTION.RIGHT);
		else
			enemy.setDirection(Core.DIRECTION.LEFT);
	}

	/**
	 Définir dans des keyframe les successions d'images qui créeront les animations du poulpe.
	 */
	private KeyFrame poulp_animation_load() {
		return new KeyFrame(Duration.millis(frameTime),
				event -> {

					if(core.getGame().isProgram_start()) {
						show_start_screen();
						return;
					}

					core.getGame_physics().move_player();
					core.getGame_physics().move_enemy();

					if(core.getGame().is_powerup_activaded())
						core.getGame().calculator();

					if(core.getPoulp().is_dead()) {
						return;
					}


					draw_hud(core.getGame().getPower_up_time());

					imageView.setOpacity(0.4);
					int poulp_animation_index = (imageIndex++) % poulp_pic.size();

					poulp_gc.clearRect(0, 0, Core.GameSize.width, Core.GameSize.height);

					Image ipoulp=null;

					if(core.getGame().is_powerup_activaded()){
						if(core.getGame().cross_walls_power()){
							previous_picture = rotate_image(angry_poulp,imageIndex%360);
						}else {
							ipoulp = angry_poulp;
						}
					}else{
						ipoulp = poulp_pic.get(poulp_animation_index);
					}

					if(!core.getGame().cross_walls_power())
						switch(core.getPoulp().getDirection()){
								case UP:
									previous_picture = rotate_image(ipoulp,180);
									break;
								case DOWN:
									previous_picture = ipoulp;
									break;
								case LEFT:
									previous_picture = rotate_image(ipoulp,90);
									break;
								case RIGHT:
								previous_picture = rotate_image(ipoulp,270);
								break;
							default:
								break;
						}

					poulp_gc.drawImage(resize_by_half(previous_picture), core.getPoulp().getCoord().getX()+(core.getPoulp().getSize().width/4), core.getPoulp().getCoord().getY()+(core.getPoulp().getSize().height/4));

					if(debug_mode) {
						DebugCanvas.setVisible(true);
						drawcollisions(core.getGame_physics().getWalls(), core.getGame_physics().getenemys(),core.getGame_physics().getitems());
						poulp_gc.setLineWidth(5);
						poulp_gc.setStroke(Color.RED);
						if(core.getPoulp().getCollisionBox()!=null)
							poulp_gc.strokeRect(core.getPoulp().getCollisionBox().getX(), core.getPoulp().getCollisionBox().getY(), core.getPoulp().getCollisionBox().getWidth(), core.getPoulp().getCollisionBox().getHeight());
					}else{
						DebugCanvas.setVisible(false);
					}
				});
	}

	/**
	 Dessine le poulpe à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	 @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void dispose_player(int matrix_i, int matrix_j){
		if(core.getPoulp()==null)
			init_player();
		core.getPoulp().setCoord(new Point((int) (matrix_i * rock.getWidth() - 10),(int) (matrix_j * rock.getHeight() - 5)));
	}

	/**
	 Dessine un ennemis à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	  * @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void dispose_enemy(int matrix_i, int matrix_j){
		Entity enemy = new Entity(new Point((int) (matrix_i * rock.getWidth()),(int) (matrix_j * rock.getHeight())), Core.wall_size,Core.DIRECTION.RIGHT);
		core.addEnemy(enemy);
	}

	/**
	 Dessine un mur à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	 @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void dispose_wall(int matrix_i, int matrix_j){
		back_gc.drawImage(rock, matrix_i * rock.getWidth(), matrix_j * rock.getHeight());
	}

	/**
	 Dessine un banc de poisson à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	 @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void dispose_fish(int matrix_i, int matrix_j){
		Entity fish = new Entity(new Point((int) (matrix_i * rock.getWidth()),(int) (matrix_j * rock.getHeight())), Core.wall_size,Core.DIRECTION.RIGHT,"fish");
		core.addItems(fish);
	}

	/**
	 Dessine un power up à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	 @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void dispose_power_up(int matrix_i, int matrix_j) {
		Entity power_up = new Entity(new Point((int) (matrix_i * rock.getWidth()),(int) (matrix_j * rock.getHeight())), Core.wall_size,Core.DIRECTION.RIGHT,"powerup");
		core.addItems(power_up);
	}

	/**
	 Efface une case à une position matricielle.
	 Typiquement, cette méthode est appelée au chargement d'un niveau.
	 @param matrix_i Emplacement sur l'axe i de la matrice
	 @param matrix_j Emplacement sur l'axe j de la matrice
	 */
	private void clear_sprite(int matrix_i, int matrix_j){
		back_gc.clearRect(matrix_i * rock.getWidth(), matrix_j * rock.getHeight(), rock.getWidth(), rock.getHeight());
	}

	/**
	 Dessine le niveau à partir de la matrice donnée en argument.
	 @param actual_level matrice qui resprésente le niveau sous forme de nombres
	 */
	public void dispose_level(ArrayList<String> actual_level){
		for(int l=0;l<actual_level.size();l++){
			String line=actual_level.get(l);
			for(int i=0;i<line.length();i++){
				if(line.charAt(i) != ' ') {
					switch (line.charAt(i)) {
						case '0':
							//clear_sprite(i,l);
							dispose_fish(i,l);
							break;
						case '1':
							dispose_wall(i,l);
							break;
						case '2':
							dispose_player(i,l);
							dispose_fish(i,l);
							break;
						case '3':
							dispose_enemy(i,l);
							dispose_fish(i,l);
							break;

						case '4':
							dispose_power_up(i,l);
							break;
						default:
							break;
					}
				}
			}
		}
	}

	public void draw_big_text(int x, int y, String text, double r, double g, double b){
		hud_gc.clearRect(0,Core.GameSize.height-40,Core.GameSize.width,40);
		poulp_gc.clearRect(0, 0, Core.GameSize.width, Core.GameSize.height);
		imageView.setOpacity(0);
		poulp_gc.setFill(Color.color(r,g,b));
		poulp_gc.fillRect(0, 0, Core.GameSize.width, Core.GameSize.height);

		poulp_gc.setFont(javafx.scene.text.Font.font("Verdana", FontWeight.EXTRA_LIGHT, 64));
		poulp_gc.setFill(Color.BLACK);
		poulp_gc.fillText(text, x, y);
	}

	public void drawcollisions(ArrayList<CollisionBox> walls, ArrayList<Entity> enemys, ArrayList<Entity> fishs) {

		if(debug_mode) {
			debug_gc.clearRect(0,0,900,720);
			debug_gc.setStroke(Color.GREEN);

			for(Entity fish:fishs){
				debug_gc.strokeRect(fish.getX()+5, fish.getY()+5, fish.getCollisionBox().getWidth()-5, fish.getCollisionBox().getHeight()-5);
			}

			debug_gc.setStroke(Color.BLUE);
			debug_gc.setLineWidth(5);
			for (CollisionBox cb : walls) {
				debug_gc.strokeRect(cb.getX(), cb.getY(), cb.getWidth(), cb.getHeight());
			}
			debug_gc.setStroke(Color.ORANGE);

			for(Entity enemy:enemys){
				debug_gc.strokeRect(enemy.getCollisionBox().getX(), enemy.getCollisionBox().getY(), enemy.getCollisionBox().getWidth(), enemy.getCollisionBox().getHeight());
			}
		}else{
			debug_gc.clearRect(0,0,900,720);
		}
	}

	public void remove_all_entity() {
		core.init_poulp(null);
		hud_gc.clearRect(0,Core.GameSize.height-40,Core.GameSize.width,40);
		poulp_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height);
		ennemy_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height);
		back_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height);
		items_gc.clearRect(0,0,Core.GameSize.width,Core.GameSize.height);
		core.deleteEnemys();
		core.deleteItems();
	}
}
