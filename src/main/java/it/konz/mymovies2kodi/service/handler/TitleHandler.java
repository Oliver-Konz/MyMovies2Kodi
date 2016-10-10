package it.konz.mymovies2kodi.service.handler;

import static it.konz.mymovies2kodi.model.MediaType.OTHER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import it.konz.mymovies2kodi.model.Disc;
import it.konz.mymovies2kodi.model.IncludesExceludes;
import it.konz.mymovies2kodi.model.MediaType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

/**
 * @author Oliver Konz - code(at)oliverkonz.de
 *
 */
public class TitleHandler extends DefaultHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private class TitleData {
		String localTitle;
		String originalTitle;
		Integer year;
		Disc.Type type;
		String set;
		String location;
		Set<String> genres = new HashSet<>();
		String imdb;
		Set<String> categories = new HashSet<>();
		boolean stereoscopic = false;
		MediaType mediaType = OTHER;
	}

	private final @Getter MultiValuedMap<MediaType, Disc> discs = new HashSetValuedHashMap<>();

	private @NonNull @Setter IncludesExceludes<String> genreInEx = new IncludesExceludes<String>();
	private @NonNull @Setter IncludesExceludes<String> locationInEx = new IncludesExceludes<String>();
	private @NonNull @Setter IncludesExceludes<String> categoryInEx = new IncludesExceludes<String>();

	private TitleData titleData = null;
	private boolean skipTitle = false;
	private String currentTag = null;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (skipTitle) {
			return;
		}

		switch (qName) {
		case "DiscTitle":
			if (titleData != null) {
				throw new IllegalStateException("Nested DiscTitle elements are not supported.");
			}
			titleData = new TitleData();
			break;

		case "ParentTitle":
			val set = attributes.getValue("Title");
			if (isNotBlank(set)) {
				titleData.set = set.trim();
			} else {
				log.warn("Parent title exists but has no title.");
			}
			break;

		case "PersonalData":
			val group = attributes.getValue("Group");
			if (!"Owned".equals(group)) {
				skipTitle = true;
				break;
			}

			val location = attributes.getValue("Location").trim();
			if (isNotBlank(location)) {
				if (locationInEx.check(location)) {
					titleData.location = location;
				} else {
					skipTitle = true;
				}
			}
			break;

		case "ChildTitle":
			skipTitle = true; // We want the child titles, not the parent.
			break;

		case "Type":
			currentTag = qName;
			String stereoscopic = attributes.getValue("BluRay3D");
			if (isNotBlank(stereoscopic)) {
				titleData.stereoscopic = Boolean.parseBoolean(stereoscopic);
			}
			break;

		case "MediaType":
		case "LocalTitle":
		case "OriginalTitle":
		case "ProductionYear":
		case "Genre":
		case "IMDB":
		case "Category":
			currentTag = qName;
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (skipTitle || currentTag == null) {
			return;
		}

		String content = new String(ch, start, length);

		if (isNotBlank(content)) {
			content = content.trim();

			switch (currentTag) {
			case "MediaType":
				titleData.mediaType = MediaType.byMyMoviesName(content);
				break;

			case "Type":
				if (titleData.type == null) { // There are other type attributes later (person) and the correct one should not be overwritten.
					titleData.type = Disc.Type.byMyMoviesType(content);
				}
				break;

			case "LocalTitle":
				titleData.localTitle = append(titleData.localTitle, content);
				break;

			case "OriginalTitle":
				titleData.originalTitle = append(titleData.originalTitle, content);
				break;

			case "ProductionYear":
				titleData.year = Integer.parseInt(content);
				break;

			case "Genre":
				titleData.genres.add(content);
				break;

			case "IMDB":
				val imdb = content.trim();
				if (!"tt0000000".equals(imdb)) {
					titleData.imdb = imdb;
				}
				break;

			case "Category":
				titleData.categories.add(content);
				break;
			}
		}
	}

	private String append(String appendTo, String value) {
		// TODO Build into setters
		if (appendTo == null) {
			return value;
		}
		if (value.equals("&")) {
			return appendTo + " & ";
		}
		return appendTo + value;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("DiscTitle".equals(qName)) {
			if (!skipTitle && genreInEx.check(titleData.genres) && categoryInEx.check(titleData.categories)) {
				val title = isNotBlank(titleData.localTitle) ? titleData.localTitle : titleData.originalTitle;
				try {
					discs.put(titleData.mediaType,
							new Disc(title, titleData.year, titleData.type, titleData.stereoscopic, titleData.set, titleData.location, titleData.imdb));
				} catch (Exception e) {
					log.error(String.format("Insufficient disc data for %s (%d).", title, titleData.year), e);
				}
			} else {
				skipTitle = false;
			}
			titleData = null;
		}
		currentTag = null;
	}

}
