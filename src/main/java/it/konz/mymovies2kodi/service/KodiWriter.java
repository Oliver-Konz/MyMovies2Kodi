package it.konz.mymovies2kodi.service;

import static it.konz.mymovies2kodi.model.MediaType.TV_SERIES;
import static it.konz.mymovies2kodi.service.KodiWriter.Mode.CLEAR;
import static it.konz.mymovies2kodi.service.KodiWriter.Mode.OVERWRITE;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.konz.mymovies2kodi.model.Disc;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

/**
 * Writes Kodi disc stubs and optionally nfos.
 * 
 * @author Oliver Konz - code(at)oliverkonz.de
 * @since 0.1.0
 */
public class KodiWriter {

	/**
	 * How to write the output files.
	 */
	public static enum Mode {

		/**
		 * Remove all existing disc stubs from the output directory.
		 */
		CLEAR,

		/**
		 * Only add new disc stubs.
		 */
		ADD,

		/**
		 * Overwrite existing disc stubs.
		 */
		OVERWRITE
	}

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final File outputDir;
	private final Mode mode;

	private @Setter @NonNull String setPrefix = "Set";
	private @Setter @NonNull String locationPrefix = "Location";
	private @Setter boolean writeImdbNfo = false;
	private @Setter long retryInterval = 2000L;
	private @Setter int maxRetries = 100;

	private boolean initialized = false;

	/**
	 * Construct a new {@link KodiWriter}.
	 * 
	 * @param outputDirName the name of the output directory (needs to exist)
	 * @param mode the write mode
	 * @since 0.1.0
	 */
	public KodiWriter(@NonNull String outputDirName, @NonNull Mode mode) {
		this.mode = mode;
		outputDir = new File(outputDirName);
		if (!outputDir.isDirectory()) {
			throw new IllegalArgumentException("The specified output directory does not exist or is not a directory.");
		}
		log.info(String.format("Writing to output dir \"%s\".", outputDir));
	}

	private void initialize() {
		if (mode == CLEAR) {
			clearOutputDir();
			try {
				Thread.sleep(retryInterval); // Network share needs some time to delete the files...
			} catch (InterruptedException e) {
				// Doesn't matter
			}
		}
		initialized = true;
	}

	private void clearOutputDir() {
		deleteFilesMatchingWildcard("*.disc");
		if (writeImdbNfo) {
			deleteFilesMatchingWildcard("*.nfo");
		}
	}

	private void deleteFilesMatchingWildcard(String wildcard) {
		log.info(String.format("Deleting %s in output dir \"%s\"...", wildcard, outputDir));

		FileFilter fileFilter = new WildcardFileFilter(wildcard);
		File[] files = outputDir.listFiles(fileFilter);

		try {
			for (File file : files) {
				Files.delete(file.toPath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error deleting files.", e);
		}
	}

	/**
	 * Write a disc stub to the output directory.
	 * 
	 * @param disc the disc
	 * @since 0.1.0
	 */
	public void write(Disc disc) {
		if (!initialized) {
			initialize();
		}

		val fileBaseName = buildFileName(disc);
		val discFileName = fileBaseName + ".disc";
		val nfoFileName = fileBaseName + ".nfo";
		val videoFileName = fileBaseName + ".mkv";

		val videoFile = new File(outputDir, videoFileName);
		if (videoFile.exists()) {
			log.info(String.format("Not writing file \"%s\" because a video file already exists.", discFileName));
			return;
		}

		val discFile = new File(outputDir, discFileName);

		int retries = 0;
		while (mode == CLEAR && discFile.exists() && retries < maxRetries) {
			log.info(String.format("Waiting for \"%s\" to be deleted.", discFileName));
			retries++;
			try {
				Thread.sleep(retryInterval);
			} catch (InterruptedException e) {
				// Doesn't matter
			}
		}

		if (!discFile.exists() || mode == OVERWRITE) {
			log.info(String.format("Writing file \"%s\"...", discFileName));
			try {
				createFile(discFile, createDiscFileContents(disc));
			} catch (IOException e) {
				throw new RuntimeException(String.format("Error writing file \"%s\".", discFileName), e);
			}
		} else {
			val message = String.format("Not writing file \"%s\" because it already exists.", discFileName);
			if (mode == CLEAR) {
				log.error(message);
			} else {
				log.info(message);
			}
		}

		if (writeImdbNfo && StringUtils.isNotBlank(disc.getImdbId())) {
			val nfoFile = new File(outputDir, nfoFileName);
			if (!nfoFile.exists() || mode == OVERWRITE) {
				log.info(String.format("Writing file \"%s\"...", nfoFileName));
				try {
					createFile(nfoFile, createNfoFileContents(disc));
				} catch (IOException e) {
					throw new RuntimeException(String.format("Error writing file \"%s\".", nfoFileName), e);
				}
			} else {
				log.debug(String.format("Not writing file \"%s\" because it already exists.", nfoFileName));
			}
		}
	}

	private String buildFileName(Disc disc) {
		final StringBuilder fileName = new StringBuilder();
		fileName.append(replaceInvalidChars(disc.getTitle()));
		if (disc.getYear() != null) {
			fileName.append(" (").append(disc.getYear()).append(')');
		}
		fileName.append('.').append(disc.getType().name().toLowerCase());
		if (disc.getBlurayAdditions().isMasteredIn4K()) {
			fileName.append(".4kmaster");
		}
		if (disc.getBlurayAdditions().isStereoscopic()) {
			fileName.append(".3d");
		}
		return fileName.toString();
	}

	private String replaceInvalidChars(String title) {
		String results = title.replace(":", " -");
		results = results.replace("\"", "");
		results = results.replace("/", "-");
		results = results.replaceAll("[%\\*<>\\?\\\\\\|;=]", "_");
		return results;
	}

	private String createDiscFileContents(Disc disc) {
		final StringBuilder contents = new StringBuilder();
		contents.append("<discstub>\n");
		if (disc.getSet() != null) {
			contents.append("\t<title>").append(setPrefix).append(": ").append(disc.getSet()).append("</title>\n");
		} else if (TV_SERIES.equals(disc.getMediaType())) {
			contents.append("\t<title>").append(setPrefix).append(": ").append(disc.getTitle()).append("</title>\n");
		}
		if (disc.getLocation() != null) {
			contents.append("\t<message>").append(locationPrefix).append(": ").append(disc.getLocation()).append("</message>\n");
		}
		contents.append("</discstub>");
		return contents.toString();
	}

	private void createFile(File file, String contents) throws IOException {
		Files.write(file.toPath(), contents.getBytes("UTF-8"));
	}

	private String createNfoFileContents(Disc disc) {
		return "http://www.imdb.com/title/" + disc.getImdbId();
	}

}
