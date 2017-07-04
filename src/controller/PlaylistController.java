package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Playlist;
import model.PlaylistMaker;
import model.Song;
import utils.Controller;

import java.awt.*;
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
	@FXML
	private Button addSongButton;
	@FXML
	private Button removeSongButton;
	@FXML
	private ListView<Song> allSongList;
	@FXML
	private ListView<Song> songList;
	@FXML
	private TextField searchField;
	@FXML
	private Label searchLabel;
	@FXML
	private Label saveNameLabel;
	@FXML
	private TextField saveNameField;
	public Button saveButton;
	@FXML
	private VBox base;

	public void initialize()
	{
		stage.setResizable(false);

		getPlaylist().getTempSongs().setAll(getPlaylist().getSongs());

		searchLabel.setLabelFor(searchField);
		saveNameLabel.setLabelFor(saveNameField);

		base.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
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
				case ((char)13): addSong(); break;
				case ((char)32): openFromList(event.getTarget()); break;
				case ((char)127): removeSong(); break;
				default: searchField.insertText(getSearch().length(), event.getCharacter());
			}
		};

		allSongList.setOnKeyTyped(listHandle);
		songList.setOnKeyTyped(listHandle);

		EventHandler<MouseEvent> listMouseHandle = event -> {
			if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY)
			{
				if(event.getSource() instanceof ListView)
				{
					if(event.getTarget() instanceof Text || event.getTarget() instanceof ListCell)
					{
						Song focused = (((ListView<Song>) event.getSource()).getFocusModel().getFocusedItem());
						if(focused != null)
							openSong(focused);
					}
				}
			}
		};

		allSongList.setOnMouseClicked(listMouseHandle);
		songList.setOnMouseClicked(listMouseHandle);

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
				case ((char)27): exit(); break;
			}
		});

		saveNameField.setOnKeyTyped(event -> {
			switch(event.getCharacter().charAt(0))
			{
				case ((char)27): exit(); break;
				case ((char)13): savePlaylist(); break;
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

	private void openFromList(EventTarget target)
	{
		if(target == allSongList)
			openSong(allSongList.getFocusModel().getFocusedItem());
		else/* if(target == songList)*/
			openSong(songList.getFocusModel().getFocusedItem());
	}

	private void openSong(Song toOpen)
	{
		Desktop desktop = Desktop.getDesktop();
		try
		{
			desktop.open(toOpen.getPath().toFile());
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private String getSearch()
	{
		return searchField.getText();
	}

	private String getPlaylistName()
	{
		return getPlaylist().getName().get();
	}

	private PlaylistMaker getPlaylistMaker()
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

	@FXML
	private void addSong()
	{
		ObservableList<Song> songs = getPlaylist().getTempSongs();
		songs.addAll(getNewSongs());
	}

	@FXML
	private void removeSong()
	{
		ObservableList<Song> songs = getPlaylist().getTempSongs();
		songs.removeAll(getSelectedSongs());
	}

	@FXML
	private void savePlaylist()
	{
		Path playlistFolderPath = Paths.get(getPlaylistMaker().getConfig().getPlaylistFolder());

		ArrayList<String> files = new ArrayList<>();
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
		exit();
	}

	@FXML
	private void exit()
	{
		getPlaylist().getTempSongs().clear();
		stage.close();
	}
}
