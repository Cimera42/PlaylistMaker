package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import model.Config;
import utils.Controller;

import java.io.IOException;

/**
 * Created by Tim on 3/07/2017 at 8:22 PM.
 */
public class ConfigController extends Controller<Config>
{
	@FXML
	private Label musicLabel;
	@FXML
	private TextField musicField;
	@FXML
	private Label playlistLabel;
	@FXML
	private TextField playlistField;
	@FXML
	private VBox base;

	public void initialize()
	{
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);

		base.setOnKeyTyped(event -> {
			System.out.println((int)event.getCharacter().charAt(0));
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): stage.close(); break;
			}
		});

		musicLabel.setLabelFor(musicField);
		playlistLabel.setLabelFor(playlistField);

		musicField.setText(getConfig().getMusicFolder());
		musicField.setPrefWidth(musicField.getText().length()*15);

		playlistField.setText(getConfig().getPlaylistFolder());
		playlistField.setPrefWidth(playlistField.getText().length()*15);

		musicField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): stage.close(); break;
				case ((char)13): playlistField.requestFocus(); break;
			}
		});

		playlistField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): stage.close(); break;
				case ((char)13): saveChanges(null); break;
			}
		});
	}

	public Config getConfig()
	{
		return model;
	}

	@FXML
	private void saveChanges()
	{
		getConfig().setMusicFolder(musicField.getText());
		getConfig().setPlaylistFolder(playlistField.getText());
		try
		{
			getConfig().save();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		stage.close();
	}

	public void exit()
	{
		stage.close();
	}
}
