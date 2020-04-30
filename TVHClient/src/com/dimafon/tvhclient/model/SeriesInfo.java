/*
 *  Copyright (C) 2020
 *
 * This file is part of TVHClient.
 *
 * TVHClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TVHClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TVHClient.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimafon.tvhclient.model;

import java.util.Locale;

public class SeriesInfo {
	public int seasonNumber;
	public int seasonCount;

	public int episodeNumber;
	public int episodeCount;

	public int partNumber;
	public int partCount;

	public String onScreen;

	public String toString() {
		if (onScreen != null && onScreen.length() > 0)
			return onScreen;

		String s = "";

		if (seasonNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("season %02d", seasonNumber);
		}
		if (episodeNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("episode %02d", episodeNumber);
		}
		if (partNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("part %d", partNumber);
		}

		if (s.length() > 0) {
			s = s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1);
		}

		return s;
	}
}
