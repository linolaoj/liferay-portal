/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Lino Alves
 */
public class ModifiedSearchFacetTermDisplayContext 
	implements Serializable {
	
	public Map<String, Object> getData() {
		return _data;
	}
	
	public int getFrequency() {
		return _frequency;
	}
	
	public String getLabel() {
		return _label;
	}

	public String getRange() {
		return _range;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}
	
	public void setFrequency(int frequency) {
		_frequency = frequency;
	}
	
	public void setLabel(String label) {
		_label = label;
	}
	
	public void setSelected(boolean selected) {
		_selected = selected;
	}
	
	public void setRange(String range) {
		_range = range;
	}

	private Map<String, Object> _data;
	
	private int _frequency;

	private String _label;

	private boolean _selected;
	
	private String _range;
	
}