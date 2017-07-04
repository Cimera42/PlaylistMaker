package model;

import java.nio.file.Path;

/**
 * Created by Tim on 2/07/2017 at 11:47 PM.
 */
public class Song
{
	private final String name;
	private final Path path;

	public Song(String name, Path path)
	{
		this.name = name;
		this.path = path;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public Path getPath()
	{
		return path;
	}
}
