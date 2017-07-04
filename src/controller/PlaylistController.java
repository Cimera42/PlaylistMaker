package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import model.Playlist;
import model.PlaylistMaker;
import model.Song;
import utils.Controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * Created by Tim on 3/07/2017 at 9:05 AM.
 */
public class PlaylistController extends Controller<Playlist>
{
	public Button addSongButton;
	public Button removeSongButton;
	public ListView<Song> allSongList;
	public ListView<Song> songList;
	public TextField searchField;
	public Label searchLabel;
	public Label saveNameLabel;
	public TextField saveNameField;
	public Button saveButton;
	public VBox base;

	public void initialize()
	{
		stage.initStyle(StageStyle.UTILITY);

		getPlaylist().getTempSongs().setAll(getPlaylist().getSongs());

		searchLabel.setLabelFor(searchField);
		saveNameLabel.setLabelFor(saveNameField);

		base.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(null); break;
			}
		});

		EventHandler<KeyEvent> listHandle = event ->
		{
			switch(event.getCharacter().charAt(0))
			{
				case ((char)8): int searchLength = getSearch().length();
								if(searchLength > 0)
									searchField.replaceText(searchLength-1, searchLength, "");
								break;
				case ((char)13): addSong(null); break;
				case ((char)127): removeSong(null); break;
				default: searchField.insertText(getSearch().length(), event.getCharacter());
			}
		};

		allSongList.setOnKeyTyped(listHandle);
		songList.setOnKeyTyped(listHandle);

		//Set width of cells so contents are shortened to '...'
		Callback<ListView<Song>, ListCell<Song>> ellipsesShortenCallback = param -> new ListCell<Song>()
		{
			{
				prefWidthProperty().bind(param.widthProperty().subtract(24));
				setMaxWidth(Control.USE_PREF_SIZE);
			}

			//Default function basically
			@Override
			protected void updateItem(Song item, boolean empty)
			{
				super.updateItem(item, empty);

				if(item != null && !empty)
				{
					setText(item.toString());
				}
				else
				{
					setText(null);
				}
			}
		};

		allSongList.setCellFactory(ellipsesShortenCallback);
		songList.setCellFactory(ellipsesShortenCallback);

		//Bindings don't trigger sometimes
		//So just set manually
		searchField.setPrefWidth(500 - 54 - 10);
		saveNameField.setPrefWidth(500 - 49 - 20 - 59);

		searchField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(null); break;
			}
		});

		saveNameField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(null); break;
				case ((char)13): savePlaylist(null); break;
			}
		});

		saveNameField.textProperty().bindBidirectional(getPlaylist().getName());
		addSongButton.setText("->");
		removeSongButton.setText("<-");

		allSongList.setPrefWidth(500);
		songList.setPrefWidth(500);

		allSongList.itemsProperty().bind(Bindings.createObjectBinding(() -> {
			ObservableList<Song> songs = getPlaylist().getTempSongs();
			return getPlaylistMaker().getFiltered(getSearch().toLowerCase(), PlaylistMaker.FileSortType.DATE).filtered(v -> !songs.contains(v));
		}, getPlaylist().getTempSongs(), searchField.textProperty()));

		allSongList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		songList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public String getSearch()
	{
		return searchField.getText();
	}

	public String getPlaylistName()
	{
		return getPlaylist().getName().get();
	}

	public PlaylistMaker getPlaylistMaker()
	{
		return model.getPlaylistMaker();
	}

	public Playlist getPlaylist()
	{
		return model;
	}

	private ObservableList<Song> getNewSongs()
	{
		return allSongList.getSelectionModel().getSelectedItems();
	}

	private ObservableList<Song> getSelectedSongs()
	{
		return songList.getSelectionModel().getSelectedItems();
	}

	public void addSong(ActionEvent actionEvent)
	{
		ObservableList<Song> songs = getPlaylist().getTempSongs();
		songs.addAll(getNewSongs());
	}

	public void removeSong(ActionEvent actionEvent)
	{
		ObservableList<Song> songs = getPlaylist().getTempSongs();
		songs.removeAll(getSelectedSongs());
	}

	public void savePlaylist(ActionEvent actionEvent)
	{
		Path playlistFolderPath = Paths.get(getPlaylistMaker().getConfig().getPlaylistFolder());

		ArrayList<String> files = new ArrayList<String>();
		getPlaylist().getTempSongs().forEach(v -> files.add(playlistFolderPath.relativize(v.getPath()).toString()));

		Path playlistPath = Paths.get(playlistFolderPath + "/" + getPlaylistName() + ".m3u");
		try
		{
			Files.createDirectories(playlistPath.getParent());
			Files.deleteIfExists(playlistPath);
			Files.write(playlistPath, files, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		} catch(IOException e)
		{
			e.printStackTrace();
		}

		//Easier just to clear and reload from file list
		//than to handle renaming/copying
		/*PlaylistMaker playlistMaker = getPlaylistMaker();
		if(!playlistMaker.getPlaylists().contains(getPlaylist()))
			playlistMaker.getPlaylists().add(getPlaylist());*/
		getPlaylist().setPath(playlistPath);

		//Songs get reloaded into playlist anyway
		//So don't need to transfer temp songs into main list
		getPlaylistMaker().reloadPlaylists();
		exit(null);
	}

	public void exit(ActionEvent actionEvent)
	{
		getPlaylist().getTempSongs().clear();
		stage.close();
	}
}
