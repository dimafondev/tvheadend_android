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

import java.util.Date;

public class Recording implements Comparable<Recording> {

    public long id;
    public Date start;
    public Date stop;
    public String title;
    public String summary;
    public String description;
    public Channel channel;
    public String state;
    public String error;

    @Override
    public int compareTo(Recording that) {
        if (this.state() == 1 && that.state() == 1) {
            return this.start.compareTo(that.start);
        } else {
            return that.start.compareTo(this.start);
        }
    }

    public boolean isRecording() {
        return state() == 0;
    }

    public boolean isScheduled() {
        return state() == 1;
    }

    private int state() {
        if ("recording".equals(state)) {
            return 0;
        } else if ("scheduled".equals(state)) {
            return 1;
        }
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Recording) {
            return ((Recording) o).id == id;
        }

        return false;
    }
}
