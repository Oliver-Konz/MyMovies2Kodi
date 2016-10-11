package it.konz.mymovies2kodi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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