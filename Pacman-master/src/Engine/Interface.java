package Engine;

import Game.Game;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class Interface {
    @FXML
    private AnchorPane GameP;

    @FXML
    private GridPane GameWindow;

    @FXML
    private CheckBox checkDebug;

    private Core core;
    private Game game;

    public void initialize() {
        GameP.setCache(true);
        GameP.setCacheShape(true);
        GameP.setCacheHint(CacheHint.SPEED);
        core = new Core(GameP);
        game = new Game(core);
        wait_key_pressed();
        game.start();
	    checkDebug.selectedProperty().addListener((observable, oldValue, newValue) -> Graphics.debug_mode = newValue);
    }

    /**
     Définit ce qu'il faut faire si un appui de touche est détecté
     */
    private void wait_key_pressed(){
        //gw.setOnKeyTyped(keyEvent -> keyPressed(keyEvent));
        //gw.setOnKeyPressed(keyEvent -> keyPressed(keyEvent));
        GameWindow.setOnKeyTyped(keyEvent -> keyPressed(keyEvent));
    }

    private void keyPressed(KeyEvent e){
        game.keyPressed(e.getCharacter());
        e.consume();
    }

}
