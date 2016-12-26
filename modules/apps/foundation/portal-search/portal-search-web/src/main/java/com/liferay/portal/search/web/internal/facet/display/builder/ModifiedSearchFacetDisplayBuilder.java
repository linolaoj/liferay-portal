package com.liferay.portal.search.web.internal.facet.display.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetFieldDisplayContext;

public class ModifiedSearchFacetDisplayBuilder {

	
	public ModifiedSearchFacetDisplayContext build()
			throws PortalException {

			ModifiedSearchFacetDisplayContext
			modifiedSearchFacetDisplayContext =
				new ModifiedSearchFacetDisplayContext();

			modifiedSearchFacetDisplayContext.setFacet(_facet);
			modifiedSearchFacetDisplayContext.setFieldParamInputValue(
				_fieldParam);

			modifiedSearchFacetDisplayContext.
			setModifiedSearchFacetFieldDisplayContexts(buildFields());

			return modifiedSearchFacetDisplayContext;
	}
	
	public ModifiedSearchFacetFieldDisplayContext buildField(
			String label, String range, int index, boolean isSelected, int frequency) {

			ModifiedSearchFacetFieldDisplayContext
			modifiedSearchFacetFieldDisplayContext =
				new ModifiedSearchFacetFieldDisplayContext();

			modifiedSearchFacetFieldDisplayContext.setFrequency(frequency);
			modifiedSearchFacetFieldDisplayContext.setSelected(isSelected);
			modifiedSearchFacetFieldDisplayContext.setLabel(label);
			modifiedSearchFacetFieldDisplayContext.setRange(range);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("selection", index);
			data.put("value", HtmlUtil.escape(range));
			
			modifiedSearchFacetFieldDisplayContext.setData(data);
			
			return modifiedSearchFacetFieldDisplayContext;
	}

	public List<ModifiedSearchFacetFieldDisplayContext> buildFields()
		throws PortalException {

		List<ModifiedSearchFacetFieldDisplayContext>
		modifiedSearchFacetFieldDisplayContexts = new ArrayList<>();

		FacetCollector facetCollector = _facet.getFacetCollector();

		for (int i = 0; i < _rangesJSONArray.length(); i++) {
			JSONObject rangesJSONObject = _rangesJSONArray.getJSONObject(i);

			String label = rangesJSONObject.getString("label");
			String range = rangesJSONObject.getString("range");

			int index = (i + 1);
		
			boolean isSelected = _fieldParam.equals(String.valueOf(index));
			
			TermCollector termCollector = facetCollector.getTermCollector(range);
			if(termCollector == null) {
				continue;
			}
			
			ModifiedSearchFacetFieldDisplayContext 
			modifiedSearchFacetFieldDisplayContext = 
				buildField(label, range, index, isSelected, termCollector.getFrequency());
		
			modifiedSearchFacetFieldDisplayContexts.add(modifiedSearchFacetFieldDisplayContext);
	
		}
	
		
		
		return modifiedSearchFacetFieldDisplayContexts;
	}
	
	
	public JSONArray getRangesJSONArray() {
		return _rangesJSONArray;
	}

	public void setRangesJSONArray(JSONArray rangesJSONArray) {
		_rangesJSONArray = rangesJSONArray;
	}

	public Facet getFacet() {
		return _facet;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public String getFieldParam() {
		return _fieldParam;
	}

	public void setFieldParam(String fieldParam) {
		_fieldParam = fieldParam;
	}

	private JSONArray _rangesJSONArray;
	private Facet _facet;
	private String _fieldParam;

}
