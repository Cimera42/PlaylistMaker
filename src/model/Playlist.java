package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Tim on 2/07/2017 at 11:47 PM.
 */
public class Playlist
{
	private final SimpleStringProperty name = new SimpleStringProperty();
	private Path path;
	private ObservableList<Song> songs = FXCollections.observableArrayList();
	private final ObservableList<Song> tempSongs = FXCollections.observableArrayList();

	private final PlaylistMaker playlistMaker;

	public Playlist(PlaylistMaker playlistMaker)
	{
		this.playlistMaker = playlistMaker;
		this.name.set("New Playlist");
	}

	public Playlist(PlaylistMaker playlistMaker, Path playlistFile) throws IOException
	{
		this.playlistMaker = playlistMaker;

		this.name.set(playlistFile.getFileName().toString().replace(".m3u", ""));
		this.path = playlistFile;

		List<String> s = Files.readAllLines(playlistFile);
		songs = FXCollections.observableArrayList(s.stream().map(v -> {
			String name = Paths.get(v).getFileName().toString();
			Path path = Paths.get(playlistMaker.getConfig().getMusicFolder() + "/" + v).normalize();
			return new Song(name, path);
		}).toArray(Song[]::new));
	}

	public String toString()
	{
		return name.get();
	}

	public StringProperty getName()
	{
		return name;
	}

	public PlaylistMaker getPlaylistMaker()
	{
		return playlistMaker;
	}

	public ObservableList<Song> getSongs()
	{
		return songs;
	}

	public Path getPath()
	{
		return path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	public ObservableList<Song> getTempSongs()
	{
		return tempSongs;
	}
}
