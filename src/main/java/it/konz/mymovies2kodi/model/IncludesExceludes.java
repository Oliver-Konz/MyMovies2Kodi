package it.konz.mymovies2kodi.model;

import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Oliver Konz - code(at)oliverkonz.de
 *
 */
@Getter
@EqualsAndHashCode
public class IncludesExceludes<T> {

	private final Set<T> includes = new HashSet<>();
	private final Set<T> excludes = new HashSet<>();

	public void addIncludes(@SuppressWarnings("unchecked") T... includes) {
		for (T include : includes) {
			this.includes.add(include);
		}
	}

	public void addExcludes(@SuppressWarnings("unchecked") T... excludes) {
		for (T exclude : excludes) {
			this.excludes.add(exclude);
		}
	}

	public boolean check(T value) {
		return check(singleton(value));
	}

	public boolean check(Collection<T> values) {
		if (!includes.isEmpty()) {
			for (T value : values) {
				if (includes.contains(value)) {
					break;
				}
			}
			return false;
		}

		if (!excludes.isEmpty()) {
			for (T value : values) {
				if (excludes.contains(value)) {
					return false;
				}
			}
		}

		return true;
	}
}
