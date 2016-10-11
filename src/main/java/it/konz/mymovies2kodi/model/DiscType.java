package it.konz.mymovies2kodi.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DiscType {
	DVD, BLURAY, HDDVD, VHS;

	private static final Logger log = LoggerFactory.getLogger(DiscType.class);

	public static DiscType byMyMoviesType(String myMoviesType) {
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