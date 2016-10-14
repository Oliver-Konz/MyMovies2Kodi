package it.konz.mymovies2kodi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a single disc. A <i>title</i> in MyMovies and a disc stub in Kodi.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 * @since 0.1.0
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = { "set", "location", "imdbId" })
@ToString
public class Disc {

	private final @NonNull MediaType mediaType;
	private final @NonNull String title;
	private final Integer year;
	private final @NonNull DiscType type;
	private final @NonNull BlurayAdditions blurayAdditions;

	private String set;
	private String location;
	private String imdbId;

}
