package it.konz.mymovies2kodi.service;

import static it.konz.mymovies2kodi.model.MediaType.DOCUMENTARY;
import static it.konz.mymovies2kodi.model.MediaType.MOVIE;
import static it.konz.mymovies2kodi.model.MediaType.MUSIC;
import static it.konz.mymovies2kodi.model.MediaType.OTHER;
import static it.konz.mymovies2kodi.model.MediaType.TV_SERIES;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections4.MultiValuedMap;
import org.xml.sax.SAXException;

import it.konz.mymovies2kodi.model.Disc;
import it.konz.mymovies2kodi.model.IncludesExceludes;
import it.konz.mymovies2kodi.model.MediaType;
import it.konz.mymovies2kodi.service.handler.TitleHandler;
import lombok.val;

/**
 * Reads the exported MyMovies collection.xml and returns a {@link Set} of {@link Disc}s.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 * @since 0.1.0
 */
public class MyMoviesReader {

	private final File importFile;
	private final IncludesExceludes<String> genreInEx = new IncludesExceludes<>();
	private final IncludesExceludes<String> locationInEx = new IncludesExceludes<>();
	private final IncludesExceludes<String> categoryInEx = new IncludesExceludes<>();

	private MultiValuedMap<MediaType, Disc> singleTitleDiscs = null;

	/**
	 * Construct the {@link MyMoviesReader} for the given import file.
	 * 
	 * @param importFileName path to the collection.xml
	 * @since 0.1.0
	 */
	public MyMoviesReader(String importFileName) {
		importFile = new File(importFileName);
		if (!importFile.exists()) {
			throw new IllegalArgumentException(String.format("Import file \"%s\" does not exist.", importFileName));
		}
		if (!importFile.canRead()) {
			throw new IllegalArgumentException(String.format("Import file \"%s\" cannot be read.", importFileName));
		}
	}

	/**
	 * Genres to include.
	 * 
	 * @param genres the genres
	 * @since 0.1.0
	 */
	public void addIncludeGenres(String... genres) {
		genreInEx.addIncludes(genres);
	}

	/**
	 * Genres to exclude.
	 * 
	 * @param genres the genres
	 * @since 0.1.0
	 */
	public void addExcludeGenres(String... genres) {
		genreInEx.addExcludes(genres);
	}

	public void addIncludeLocations(String... locations) {
		locationInEx.addIncludes(locations);
	}

	public void addExcludeLocations(String... locations) {
		locationInEx.addExcludes(locations);
	}

	public void addIncludeCategories(String... categories) {
		categoryInEx.addIncludes(categories);
	}

	public void addExcludeCategories(String... categories) {
		categoryInEx.addExcludes(categories);
	}

	/**
	 * Parse the collection.xml for movie titles.
	 * 
	 * @return the discs
	 * @since 0.1.0
	 */
	public Set<Disc> readMovies() {
		return getTitles(MOVIE);
	}

	/**
	 * Parse the collection.xml for documentary titles.
	 * 
	 * @return the discs
	 * @since 0.1.0
	 */
	public Set<Disc> readDocumentaries() {
		return getTitles(DOCUMENTARY);
	}

	/**
	 * Parse the collection.xml for music titles.
	 * 
	 * @return the discs
	 * @since 0.1.0
	 */
	public Set<Disc> readMusicVideos() {
		return getTitles(MUSIC);
	}

	/**
	 * Parse the collection.xml for TV series titles.
	 * 
	 * @return the discs
	 * @since 0.1.0
	 */
	public Set<Disc> readTvSeries() {
		return getTitles(TV_SERIES);
	}

	/**
	 * Parse the collection.xml for music titles.
	 * 
	 * @return the discs
	 * @since 0.1.0
	 */
	public Set<Disc> readOtherVideos() {
		return getTitles(OTHER);
	}

	private Set<Disc> getTitles(MediaType mediaType) {
		if (singleTitleDiscs == null) {
			singleTitleDiscs = readTitles();
		}
		return (Set<Disc>) singleTitleDiscs.get(mediaType);
	}

	private MultiValuedMap<MediaType, Disc> readTitles() {
		val titleHandler = new TitleHandler();
		titleHandler.setGenreInEx(genreInEx);
		titleHandler.setLocationInEx(locationInEx);
		titleHandler.setCategoryInEx(categoryInEx);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(importFile, titleHandler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException("Error reading movies from MyMovies Collection.xml", e);
		}
		return titleHandler.getDiscs();
	}
}
