/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.openalbum.mixare.gui;

import org.openalbum.mixare.DataView;
import org.openalbum.mixare.data.DataHandler;
import org.openalbum.mixare.marker.Marker;

import android.graphics.Color;

/**
 * Takes care of the small radar in the top left corner and of its points
 * 
 * @author daniele
 * 
 */
public class RadarPoints implements ScreenObj {
	/** The screen */
	public DataView view;
	/** The radar's range */
	float range;
	/** Radius in pixel on screen */
	private static float RADIUS = 40;
	/** Position on screen */
	static float originX = 0, originY = 0;
	/** Color */
	static int radarColor = Color.argb(100, 0, 0, 200);

	public void paint(final PaintScreen dw) {
		/** radius is in KM. */
		range = view.getRadius() * 1000;
		/** Draw the radar */
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + getRADIUS(), originY + getRADIUS(),
				getRADIUS());

		/** put the markers in it */
		final float scale = range / getRADIUS();

		final DataHandler jLayer = view.getDataHandler();

		for (int i = 0; i < jLayer.getMarkerCount(); i++) {
			final Marker pm = jLayer.getMarker(i);
			final float x = pm.getLocationVector().x / scale;
			final float y = pm.getLocationVector().z / scale;

			if (pm.isActive() && (x * x + y * y < getRADIUS() * getRADIUS())) {
				dw.setFill(true);

				// For OpenStreetMap the color is changing based on the URL
				dw.setColor(pm.getColour());

				dw.paintRect(x + getRADIUS() - 1, y + getRADIUS() - 1, 2, 2);
			}
		}
	}

	/** Width on screen */
	public float getWidth() {
		return getRADIUS() * 2;
	}

	/** Height on screen */
	public float getHeight() {
		return getRADIUS() * 2;
	}

	/**
	 * @return the rADIUS
	 */
	public static float getRADIUS() {
		return RADIUS;
	}

	/**
	 * @param rADIUS
	 *            the rADIUS to set
	 */
	public static void setRADIUS(final float rADIUS) {
		RADIUS = rADIUS;
	}
}
