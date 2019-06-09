package org.ringingmaster.ui.desktop;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ringingmaster.util.javafx.lifecycle.StartupService;
import org.ringingmaster.util.thread.ThreadUncaughtExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Spring based launcher for the JavaFX App.
 * <p>
 * NOTE: It would be preferred to have a stand alone initialiser class that has the Application as a first class Spring
 * bean. However the rather dumb way that JavaFX gets you to call the static launch(), then creates an
 * Application class, but does not pass it back (for use in a Spring factory) means that we have to let JavaFX do its
 * thing then hook into the callbacks to glue everything together.
 *
 * @author Steve Lake
 */
public class RingingMasterDesktopApp extends Application {


    private final static Logger log = LoggerFactory.getLogger(RingingMasterDesktopApp.class);
    public static final String STYLESHEET = "org/ringingmaster/ui/desktop/RingingmasterDesktopApp.css";

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();
        log.info("Starting...");
        launch(RingingMasterDesktopApp.class, args);
        log.info("Ending. Byeeee...");
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage stage) throws IOException {
        StageFactory.globalStage = stage;
        applicationContext = new ClassPathXmlApplicationContext("spring/appCtx-ringingmaster-ui-desktop.xml");
        Parent parent = (Parent) applicationContext.getBean("mainWindow");

        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        double x = userPrefs.getDouble("globalStage.x", 100);
        double y = userPrefs.getDouble("globalStage.y", 100);
        double width = userPrefs.getDouble("globalStage.width", 900);
        double height = userPrefs.getDouble("globalStage.height", 650);

        log.info("Main window bounds before modification, [{},{},{},{}]", x, y, width, height);

        // Make sure the screen has some width and height
        width = Math.max(100, width);
        height = Math.max(100, height);

        // TODO need to test on duel monitors - preferably with different sizes.
        // Make sure the width and height is no bigget than the screen.
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        width = Math.min(primaryScreenBounds.getWidth(), width);
        height = Math.min(primaryScreenBounds.getHeight(), height);

        // TODO need to test on duel monitors - preferably with different sizes.
        // Make sure the origin is not off the screen.
        x = Math.max(0, x);
        y = Math.max(0, y);
        x = Math.min(primaryScreenBounds.getMaxX() - 10, x);
        y = Math.min(primaryScreenBounds.getMaxY() - 10, y);

        log.info("Main window bounds after modification, [{},{},{},{}]", x, y, width, height);

        Scene scene = new Scene(parent, width, height);
        scene.getStylesheets().add(STYLESHEET);
        stage.setScene(scene);
        stage.setX(x);
        stage.setY(y);
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(RingingMasterDesktopApp.class.getResourceAsStream("/images/RingingMaster.png")));
        stage.setTitle("Ringingmaster Desktop");

        applicationContext.getBean(StartupService.class).runStartup();

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