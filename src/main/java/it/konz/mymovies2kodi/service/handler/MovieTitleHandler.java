package it.konz.mymovies2kodi.service.handler;

/**
 * SAX handler implementation for movie titles in the collection.xml.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 */
public class MovieTitleHandler extends SingleTitleHandler {

	@Override
	protected boolean checkMediaType(String mediaType) {
		return "Movie".equals(mediaType);
	}

}
