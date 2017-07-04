package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Config;
import utils.Controller;
import utils.PrintablePair;

import java.io.IOException;

/**
 * Created by Tim on 3/07/2017 at 8:22 PM.
 */
public class ConfigController extends Controller<Config>
{
	@FXML
	public Label openMethodLabel;
	@FXML
	private ComboBox<PrintablePair<Boolean,String>> openFileMethodCombo;
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
		stage.setResizable(false);

		base.setOnKeyTyped(event -> {
			//System.out.println((int)event.getCharacter().charAt(0));
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
			}
		});

		musicLabel.setLabelFor(musicField);
		playlistLabel.setLabelFor(playlistField);
		openMethodLabel.setLabelFor(openFileMethodCombo);

		musicField.setText(getConfig().getMusicFolder());
		musicField.setPrefWidth(musicField.getText().length()*15);

		playlistField.setText(getConfig().getPlaylistFolder());
		playlistField.setPrefWidth(playlistField.getText().length()*15);

		musicField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
				case ((char)13): playlistField.requestFocus(); break;
			}
		});

		playlistField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
				case ((char)13): saveChanges(); break;
			}
		});

		ObservableList<PrintablePair<Boolean, String>> options = FXCollections.observableArrayList();
		options.add(new PrintablePair<>(true, "Open File"));
		options.add(new PrintablePair<>(false, "Open Folder"));
		openFileMethodCombo.setItems(options);

		options.forEach(v -> {
			if(v.getKey() == getConfig().getShouldOpenFile())
				openFileMethodCombo.getSelectionModel().select(v);
		});
	}

	private Config getConfig()
	{
		return model;
	}

	@FXML
	private void saveChanges()
	{
		getConfig().setMusicFolder(musicField.getText());
		getConfig().setPlaylistFolder(playlistField.getText());
		getConfig().setShouldOpenFile(openFileMethodCombo.getSelectionModel().getSelectedItem().getKey());
		try
		{
			getConfig().save();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		exit();
	}

	public void exit()
	{
		stage.close();
	}
}
