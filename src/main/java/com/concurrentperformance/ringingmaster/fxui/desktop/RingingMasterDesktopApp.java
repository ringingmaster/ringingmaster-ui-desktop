package com.concurrentperformance.ringingmaster.fxui.desktop;

import com.concurrentperformance.fxutils.lifecycle.StartupService;
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

	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();
		log.info("Starting...");
		launch(RingingMasterDesktopApp.class, args);
		log.info("Ending...");
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void start(Stage stage) throws IOException {
		StageFactory.globalStage = stage;
		applicationContext = new ClassPathXmlApplicationContext("spring/appCtx-ringingmaster-ui-desktop.xml");
		Parent parent = (Parent)applicationContext.getBean("mainWindow");

		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		double x = userPrefs.getDouble("globalStage.x", 100);
		double y = userPrefs.getDouble("globalStage.y", 100);
		double width = userPrefs.getDouble("globalStage.width", 900);
		double height = userPrefs.getDouble("globalStage.height", 650);

		Scene scene = new Scene(parent, width,height);
		scene.getStylesheets().add(STYLESHEET);
		stage.setScene(scene);
		stage.setX(x);
		stage.setY(y);
		stage.initStyle(StageStyle.DECORATED);
		stage.getIcons().add(new Image(	RingingMasterDesktopApp.class.getResourceAsStream("/images/RingingMaster.png")));
		stage.setTitle("Ringingmaster Desktop");

		applicationContext.getBean(StartupService.class).runStartup();;

		stage.show();

		log.info("Started...");

	}

	@Override
	public void stop() throws Exception {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		userPrefs.putDouble("globalStage.x", StageFactory.globalStage.getX());
		userPrefs.putDouble("globalStage.y", StageFactory.globalStage.getY());
		userPrefs.putDouble("globalStage.width", StageFactory.globalStage.getWidth());
		userPrefs.putDouble("globalStage.height", StageFactory.globalStage.getHeight());
	}
}