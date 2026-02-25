package com.tuan.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tuan.game.MyGdxGame;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // cấu hình cửa sổ game
        config.setTitle("The Syntax Survivor");
        config.setWindowedMode(1280, 720);
        config.useVsync(true); // Chống xé hình
        config.setForegroundFPS(60); // Giới hạn 60 khung hình/giây để tiết kiệm CPU

        new Lwjgl3Application(new MyGdxGame(), config);
    }
}
