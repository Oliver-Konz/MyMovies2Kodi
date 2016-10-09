package it.konz.mymovies2kodi.service.handler;

/**
 * SAX handler implementation for documentary titles in the collection.xml.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 */
public class DocumentaryTitleHandler extends SingleTitleHandler {

	@Override
	protected boolean checkMediaType(String mediaType) {
		return "Documentary".equals(mediaType);
	}

}
