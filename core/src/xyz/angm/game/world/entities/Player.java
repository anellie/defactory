package xyz.angm.game.world.entities;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** The player in the game. Their goal is building a base to defend against beasts.*/
public class Player extends Entity {

    private static final int PLAYER_HEALTH = 20;

    public Player() {
        health = PLAYER_HEALTH;
        actor = new Image(/* TODO Add player image*/);
    }
}
