package com.jamestkhan.game;

import com.badlogic.gdx.Game;
import com.jamestkhan.game.screens.BaseScreen;

public class MyGdxGame extends Game {
	
	@Override
	public void create () {
		setScreen(new BaseScreen());
	}


}
