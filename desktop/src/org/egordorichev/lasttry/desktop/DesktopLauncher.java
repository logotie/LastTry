package org.egordorichev.lasttry.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.egordorichev.lasttry.LastTry;
import org.egordorichev.lasttry.core.boot.ArgumentParser;
import org.egordorichev.lasttry.util.log.Log;

/** LastTry launcher */
public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 800;
		config.height = 600;
		config.vSyncEnabled = true;
		config.fullscreen = false;
		config.resizable = true;

		// config.addIcon("icon.png", Files.FileType.Internal);

		try {
			ArgumentParser parser = new ArgumentParser(args);
			parser.parse();
		} catch (RuntimeException exception) {
			Log.error(exception.getMessage());
			Log.error("Failed to parse arguments aborting");
			return;
		}

		new LwjglApplication(new LastTry());
	}
}