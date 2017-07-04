package controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import model.Config;
import utils.Controller;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Tim on 3/07/2017 at 8:22 PM.
 */
public class ConfigController extends Controller<Config>
{
	public Label musicLabel;
	public TextField musicField;
	public Label playlistLabel;
	public TextField playlistField;
	public VBox base;

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

	public void saveChanges(ActionEvent actionEvent)
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

	public void exit(ActionEvent actionEvent)
	{
		stage.close();
	}
}
