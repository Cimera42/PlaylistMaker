/**
 * Created by Tim on 2/07/2017 at 11:46 PM.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Playlist;
import model.PlaylistMaker;
import utils.FxmlLoad;

import java.io.IOException;

public class Main extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		String fxmlFile = "/view/main.fxml";
		FxmlLoad.fxmlWindow(new PlaylistMaker(), fxmlFile, "Playlist Maker", primaryStage);
		primaryStage.setOnCloseRequest(e -> Platform.exit());
	}
}
