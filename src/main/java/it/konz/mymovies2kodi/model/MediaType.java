package it.konz.mymovies2kodi.model;

import java.util.Map;

import org.apache.commons.collections4.map.DefaultedMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MediaType {
	MOVIE("Movie"),
	DOCUEMENTARY("Documentary"),
	MUSIC("Music"),
	TV_SERIES("TV Series"),
	OTHER("Other");
	
	private static Map<String, MediaType> mapByMyMoviesName;
	
	public static MediaType byMyMoviesName(String name) {
		if (mapByMyMoviesName == null) {
			mapByMyMoviesName = new DefaultedMap<String, MediaType>(OTHER);
			for (MediaType value : values()) {
				mapByMyMoviesName.put(value.myMoviesName, value);
			}
		}
		return mapByMyMoviesName.get(name);
	}
	
	private final @Getter String myMoviesName;
	
}
