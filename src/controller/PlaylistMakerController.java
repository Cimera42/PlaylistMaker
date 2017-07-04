package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Config;
import model.Playlist;
import model.PlaylistMaker;
import utils.Controller;
import utils.FxmlLoad;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Tim on 2/07/2017 at 11:48 PM.
 */
public class PlaylistMakerController extends Controller<PlaylistMaker>
{
	@FXML
	private ListView<Playlist> playlistList;
	@FXML
	private VBox base;

	public void initialize()
	{
		stage.setResizable(false);

		base.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
			}
		});

		EventHandler<KeyEvent> listHandle = event ->
		{
//			System.out.println((int)event.getCharacter().charAt(0));
			switch(event.getCharacter().charAt(0))
			{
				case ((char)13): editPlaylist(); break;
				case ((char)32): openPlaylist(); break;
				case ((char)127): deletePlaylist(getSelectedPlaylist()); break;
			}
		};

		playlistList.setOnKeyTyped(listHandle);
		playlistList.setOnMouseClicked(event -> {
			if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY)
			{
				//System.out.println(event);
				if(event.getTarget() instanceof Text/* || event.getTarget() instanceof ListCell*/)
					editPlaylist();
				else
					newPlaylist();
			}
		});
	}

	private void openPlaylist()
	{
		Config config = getPlaylistMaker().getConfig();
		Desktop desktop = Desktop.getDesktop();
		Playlist selectedPlaylist = getSelectedPlaylist();
		try
		{
			if(config.getShouldOpenFile())
			{
				desktop.open(selectedPlaylist.getPath().toFile());
			}
			else
			{
				desktop.open(selectedPlaylist.getPath().getParent().toFile());
			}
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void deletePlaylist(Playlist selectedPlaylist)
	{
		if(selectedPlaylist != null)
		{
			try
			{
				Files.delete(selectedPlaylist.getPath());
				getPlaylistMaker().getPlaylists().remove(selectedPlaylist);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public PlaylistMaker getPlaylistMaker()
	{
		return model;
	}

	private Playlist getSelectedPlaylist()
	{
		return playlistList.getSelectionModel().getSelectedItem();
	}

	@FXML
	private void newPlaylist()
	{
		String fxmlFile = "/view/playlist.fxml";
		try
		{
			FxmlLoad.fxmlWindow(new Playlist(getPlaylistMaker()), fxmlFile, "New Playlist", new Stage());
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void editPlaylist()
	{
		Playlist selectedPlaylist = getSelectedPlaylist();
		if(selectedPlaylist != null)
		{
			String fxmlFile = "/view/playlist.fxml";
			try
			{
				FxmlLoad.fxmlWindow(selectedPlaylist, fxmlFile, "Edit Playlist", new Stage());
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void openConfig() throws IOException
	{
		String fxmlFile = "/view/config.fxml";
		FxmlLoad.fxmlWindow(getPlaylistMaker().getConfig(), fxmlFile, "Config", new Stage());
	}

	public void exit()
	{
		stage.close();
	}
}
