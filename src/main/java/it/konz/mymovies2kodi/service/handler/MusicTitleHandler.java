package it.konz.mymovies2kodi.service.handler;

/**
 * SAX handler implementation for music titles in the collection.xml.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 */
public class MusicTitleHandler extends SingleTitleHandler {

	@Override
	protected boolean checkMediaType(String mediaType) {
		return "Music".equals(mediaType);
	}

}
