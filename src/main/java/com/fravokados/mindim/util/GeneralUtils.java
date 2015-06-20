package com.fravokados.mindim.util;

import net.minecraft.util.StatCollector;

import java.util.Arrays;
import java.util.List;

/**
 * Some of this code is adapted from <a href="https://github.com/micdoodle8/Galacticraft/blob/master/src/main/java/micdoodle8/mods/galacticraft/core/util/GCCoreUtil.java">Galacticraft</a><br>
 * Provides general Utility Functions
 *
 * @author Nuklearwurst
 */
public class GeneralUtils {

	/**
	 * translates a key to the current language stripping comment
	 * @param key the key that should get tramslated
	 * @return the translated String
	 */
	public static String translate(String key)
	{
		String result = StatCollector.translateToLocal(key);
		int comment = result.indexOf('#');
		return (comment > 0) ? result.substring(0, comment).trim() : result;
	}

	/**
	 * translates and splits a key to the current language stripping comment
	 * @param key the key that should get tramslated
	 * @return the translated String
	 */
	public static List<String> translateWithSplit(String key)
	{
		String translated = translate(key);
		int comment = translated.indexOf('#');
		translated = (comment > 0) ? translated.substring(0, comment).trim() : translated;
		return Arrays.asList(translated.split("\\$"));
	}

	/**
	 * translates and formats a key to the current language stripping comment
	 * @param key the key that should get tramslated
	 * @return the translated String
	 */
	public static String translateWithFormat(String key, Object... values)
	{
		String result = StatCollector.translateToLocalFormatted(key, values);
		int comment = result.indexOf('#');
		return (comment > 0) ? result.substring(0, comment).trim() : result;
	}
}