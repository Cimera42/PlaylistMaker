package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Tim on 3/07/2017 at 12:00 AM.
 */
public class FxmlLoad
{
	public static <T> void fxmlWindow(T model, String fxml, String title, Stage stage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxml), null, null,
				type -> {
					try {
						Controller<T> controller = (Controller<T>)type.newInstance();
						controller.model = model;
						controller.stage = stage;
						return controller;
					} catch (Exception e) { throw new RuntimeException(e); }
				});
		Parent root = loader.load();
		stage.setTitle(title);
		stage.setScene(new Scene(root));
		stage.sizeToScene();
		stage.show();
	}
}
