package it.konz.mymovies2kodi.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public enum Type {
		DVD, BLURAY, HDDVD, VHS;

		private static final Logger log = LoggerFactory.getLogger(Type.class);

		public static Type byMyMoviesType(String myMoviesType) {
			// TODO What about UHD Blurays?
			switch (myMoviesType) {
			case "Blu-ray":
				return BLURAY;
			case "DVD":
				return DVD;
			case "HDDVD": // TODO Check - I have none in my collection.
				return HDDVD;
			default:
				log.warn("Unknown MyMovies type: " + myMoviesType);
				return VHS; // ;-)
			}
		}
	}

	@RequiredArgsConstructor
	public enum BlurayAdditions {
		NO_ADDITIONS(false, false), STEREOSCOPIC(true, false), MASTERED_IN_4K(false, true), STEREOSCOPIC_MASTERED_IN_4K(true, true);

		public static BlurayAdditions byStereo4K(boolean stereoscopic, boolean masteredIn4K) {
			if (stereoscopic && !masteredIn4K) {
				return STEREOSCOPIC;
			}
			if (stereoscopic && masteredIn4K) {
				return STEREOSCOPIC_MASTERED_IN_4K;
			}
			if (!stereoscopic && masteredIn4K) {
				return BlurayAdditions.MASTERED_IN_4K;
			}
			return NO_ADDITIONS;
		}

		private final @Getter boolean stereoscopic;
		private final @Getter boolean masteredIn4K;
	}

	private final @NonNull String title;
	private final Integer year;
	private final @NonNull Type type;
	private final @NonNull BlurayAdditions blurayAdditions;

	private String set;
	private String location;
	private String imdbId;
}
