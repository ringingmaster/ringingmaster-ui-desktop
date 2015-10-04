package com.concurrentperformance.ringingmaster.fxui.desktop;

import com.concurrentperformance.util.thread.ThreadUncaughtExceptionHelper;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Spring based launcher for the JavaFX App.
 *
 * NOTE: It would be preferred to have a stand alone initialiser class that has the Application as a first class Spring
 * bean. However the rather dumb way that JavaFX gets you to call the static launch(), then creates an
 * Application class, but does not pass it back (for use in a Spring factory) means that we have to let JavaFX do its
 * thing then hook into the callbacks to glue everything together.
 *
 * @author Lake
 */
public class RingingMasterDesktopApp extends Application {


	private final static Logger log = LoggerFactory.getLogger(RingingMasterDesktopApp.class);
	public static final String STYLESHEET = "com/concurrentperformance/ringingmaster/fxui/desktop/RingingmasterDesktopApp.css";


	public static void main(String[] args) {
		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();
		log.info("Starting RingingMasterDesktopApp");
		launch(RingingMasterDesktopApp.class, args);
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void start(Stage stage) throws IOException {
		StageFactory.primaryStage = stage;
		final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/appCtx-ringingmaster-ui-desktop.xml");
		Parent parent = (Parent)applicationContext.getBean("mainWindow");

		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		double x = userPrefs.getDouble("primaryStage.x", 100);
		double y = userPrefs.getDouble("primaryStage.y", 100);
		double width = userPrefs.getDouble("primaryStage.width", 900);
		double height = userPrefs.getDouble("primaryStage.height", 650);

		Scene scene = new Scene(parent, width,height);
		scene.getStylesheets().add(STYLESHEET);
		stage.setScene(scene);
		stage.setX(x);
		stage.setY(y);
		stage.initStyle(StageStyle.DECORATED);
		stage.getIcons().add(new Image(	RingingMasterDesktopApp.class.getResourceAsStream("/images/RingingMaster.png")));
		stage.setTitle("Ringingmaster Desktop");

		stage.show();
	}

	@Override
	public void stop() throws Exception {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		userPrefs.putDouble("primaryStage.x", StageFactory.primaryStage.getX());
		userPrefs.putDouble("primaryStage.y", StageFactory.primaryStage.getY());
		userPrefs.putDouble("primaryStage.width", StageFactory.primaryStage.getWidth());
		userPrefs.putDouble("primaryStage.height", StageFactory.primaryStage.getHeight());
	}
}