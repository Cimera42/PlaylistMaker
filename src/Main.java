import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.PlaylistMaker;
import utils.FxmlLoad;

import java.io.IOException;

/**
 * Created by Tim on 2/07/2017 at 11:46 PM.
 */
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
