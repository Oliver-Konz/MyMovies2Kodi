package it.konz.mymovies2kodi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.konz.mymovies2kodi.model.Disc;
import it.konz.mymovies2kodi.service.KodiWriter;
import it.konz.mymovies2kodi.service.KodiWriter.Mode;
import it.konz.mymovies2kodi.service.MyMoviesReader;

/**
 * @author Oliver Konz - code(at)oliverkonz.de
 *
 */
public class FirstTest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void spielfilmeTest() {
		MyMoviesReader reader = new MyMoviesReader("target/test-classes/Collection.xml");
		reader.addExcludeLocations("Dachzimmer");
		reader.addExcludeCategories("Digital in HD");
		Set<Disc> discs = reader.readMovies();
		assertThat(discs, not(empty()));
		log.info(String.format("There are %d movie discs in the collection.", discs.size()));

		KodiWriter writer = new KodiWriter("Z:/Discs/Spielfilme", Mode.CLEAR);
		for (Disc disc : discs) {
			writer.write(disc);
		}
	}

	@Test
	public void dokusTest() {
		MyMoviesReader reader = new MyMoviesReader("target/test-classes/Collection.xml");
		Set<Disc> discs = reader.readDocumentaries();
		assertThat(discs, not(empty()));
		log.info(String.format("There are %d docu discs in the collection.", discs.size()));

		KodiWriter writer = new KodiWriter("Z:/Discs/Dokus", Mode.CLEAR);
		for (Disc disc : discs) {
			writer.write(disc);
		}
	}

	@Test
	public void musikTest() {
		MyMoviesReader reader = new MyMoviesReader("target/test-classes/Collection.xml");
		Set<Disc> discs = reader.readMusicVideos();
		assertThat(discs, not(empty()));
		log.info(String.format("There are %d music discs in the collection.", discs.size()));

		KodiWriter writer = new KodiWriter("Z:/Discs/Musik", Mode.CLEAR);
		// writer.setWriteImdbNfo(true);
		for (Disc disc : discs) {
			writer.write(disc);
		}
	}
}
