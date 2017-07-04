package model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

/**
 * Created by Tim on 2/07/2017 at 11:58 PM.
 */
public class PlaylistMaker
{
	public enum FileSortType {ALPHABETICAL, DATE}

	private final ObservableList<Playlist> playlists = FXCollections.observableArrayList(i -> new Observable[]{i.getName()});
	private final ObservableList<Song> allSongs = FXCollections.observableArrayList();

	private final Config config;

	public PlaylistMaker()
	{
		config = new Config(this, getClass().getResource("/config.json"));

		reloadAll();
	}

	private void findSongs(Path path) throws IOException
	{
		if(path.toFile().isDirectory())
		{
			Files.newDirectoryStream(path)
					.forEach(newPath ->
					{
						File newFile = newPath.toFile();
						if(newFile.isDirectory())
						{
							try
							{
								findSongs(newPath);
							} catch(IOException e)
							{
								e.printStackTrace();
							}
						}
						else if(newFile.isFile())
						{
							if(newFile.getName().endsWith(".mp3"))
								allSongs.add(new Song(newPath.getFileName().toString(), newPath));
						}
					});
		}
	}

	private void findPlaylists(Path path) throws IOException
	{
		if(path.toFile().isDirectory())
		{
			Files.newDirectoryStream(path)
					.forEach(newPath ->
					{
						File newFile = newPath.toFile();
						if(newFile.isFile())
						{
							if(newFile.getName().endsWith(".m3u"))
							{
								try
								{
									playlists.add(new Playlist(this, newPath));
								} catch(IOException e)
								{
									e.printStackTrace();
								}
							}
						}
					});
		}
	}

	public ObservableList<Playlist> getPlaylists()
	{
		return playlists;
	}
	public ObservableList<Song> getAllSongs()
	{
		return allSongs;
	}
	public Config getConfig()
	{
		return config;
	}

	public ObservableList<Song> getFiltered(String filterString, FileSortType sortType)
	{
		ObservableList<Song> sorted = FXCollections.observableArrayList();
		if(sortType == FileSortType.ALPHABETICAL)
		{
			sorted = FXCollections.observableArrayList(allSongs.sorted(Comparator.comparing(Song::getName)));
		}
		else if(sortType == FileSortType.DATE)
		{
			sorted = FXCollections.observableArrayList(allSongs.sorted((songA, songB) ->
					{
						try
						{
							BasicFileAttributes attrA = Files.readAttributes(songA.getPath(), BasicFileAttributes.class);
							BasicFileAttributes attrB = Files.readAttributes(songB.getPath(), BasicFileAttributes.class);
							return attrA.creationTime().compareTo(attrB.creationTime());
						} catch(IOException e)
						{
							e.printStackTrace();
						}
						return 0;
					}
			));
		}
		sorted = sorted.filtered(v -> v.getName().toLowerCase().contains(filterString));
		return sorted;
	}

	public void reloadAll()
	{
		reloadSongs();
		reloadPlaylists();
	}

	public void reloadPlaylists()
	{
		playlists.clear();
		try
		{
			findPlaylists(Paths.get(config.getPlaylistFolder()));
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void reloadSongs()
	{
		allSongs.clear();
		try
		{
			findSongs(Paths.get(config.getMusicFolder()));
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
