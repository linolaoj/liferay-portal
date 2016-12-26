package com.liferay.portal.search.web.internal.facet.display.context;

import java.util.Map;

public class ModifiedSearchFacetFieldDisplayContext {

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

	public void setRange(String range) {
		_range = range;
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	private int _frequency;
	private String _label;
	private String _range;
	private boolean _selected;
	private Map<String, Object> _data;

}
