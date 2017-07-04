package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Tim on 3/07/2017 at 8:20 PM.
 */
public class Config
{
	private final PlaylistMaker playlistMaker;

	private SimpleStringProperty musicFolder;
	private SimpleStringProperty playlistFolder;

	private Path path;

	public Config(PlaylistMaker playlistMaker)
	{
		this.playlistMaker = playlistMaker;

		try
		{
			path = Paths.get("./config.json");
			//Plain ./ doesn't load in dev mode, but does when as a jar
			//and vice versa, getResource doesn't load as jar
			if(!path.toFile().exists())
				path = Paths.get(getClass().getResource("/config.json").toURI());
			String configString = new String(Files.readAllBytes(path));
			ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
			Map parsed = (Map) scriptEngine.eval("Java.asJSONCompatible(" + configString + ")");
			musicFolder = new SimpleStringProperty((String) parsed.get("music"));
			playlistFolder = new SimpleStringProperty((String) parsed.get("playlists"));

		} catch(ScriptException | IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	public String getMusicFolder()
	{
		return musicFolder.get();
	}
	public void setMusicFolder(String newFolder)
	{
		musicFolder.set(newFolder);
	}

	public StringProperty musicFolderProperty()
	{
		return musicFolder;
	}

	public String getPlaylistFolder()
	{
		return playlistFolder.get();
	}
	public void setPlaylistFolder(String newFolder)
	{
		playlistFolder.set(newFolder);
	}

	public StringProperty playlistFolderProperty()
	{
		return playlistFolder;
	}

	public void save() throws IOException
	{
		String toWrite = "{\n";
		toWrite += "\t\"music\":\"" + musicFolder.get() + "\",\n";
		toWrite += "\t\"playlists\":\"" + playlistFolder.get() + "\"\n";
		toWrite += "}";
		Files.write(path, toWrite.getBytes(Charset.forName("UTF-8")));

		playlistMaker.reloadAll();
	}
}
