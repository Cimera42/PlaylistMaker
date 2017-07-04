package utils;

import javafx.util.Pair;

/**
 * Created by Tim on 4/07/2017 at 9:08 PM.
 * Simply a Pair that gives the Value part as its toString
 */
public class PrintablePair<K, V> extends Pair<K, V>
{
	/**
	 * Creates a new pair
	 *
	 * @param key   The key for this pair
	 * @param value The value to use for this pair
	 */
	public PrintablePair(K key, V value)
	{
		super(key, value);
	}

	public String toString()
	{
		return getValue().toString();
	}
}
