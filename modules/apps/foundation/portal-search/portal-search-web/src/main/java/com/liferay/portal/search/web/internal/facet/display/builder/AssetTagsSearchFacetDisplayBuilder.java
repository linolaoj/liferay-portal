package com.liferay.portal.search.web.internal.facet.display.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetTermDisplayContext;

public class AssetTagsSearchFacetDisplayBuilder {

	public AssetTagsSearchFacetDisplayContext build() {
		AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
			new AssetTagsSearchFacetDisplayContext();

		assetTagsSearchFacetDisplayContext.setParamName(_paramName);
		assetTagsSearchFacetDisplayContext.setParamValue(_paramValue);

		
		boolean nothingSelected = false;

		if (Validator.isBlank(_paramValue)) {
			nothingSelected = true;
		}

		assetTagsSearchFacetDisplayContext.setNothingSelected(nothingSelected);

		List<TermCollector> termCollectors = getTermsCollectors();

		if (nothingSelected && termCollectors.isEmpty()) {
			assetTagsSearchFacetDisplayContext.setRenderNothing(true);
		}

		assetTagsSearchFacetDisplayContext.setFacetLabel(getFacetLabel());
		
		assetTagsSearchFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts());

		return assetTagsSearchFacetDisplayContext;
	}

	protected String getFacetLabel() {
		return _facet.getFacetConfiguration().getLabel();
	}
	
	protected boolean isRenderNothing() {
		if (!Validator.isBlank(_paramValue)) {
			return false;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		if (!termCollectors.isEmpty()) {
			return false;
		}

		return true;
	}
	
	protected double getPopularity(
			int frequency, int minCount, int maxCount, double multiplier) {

			double popularity = maxCount - (maxCount - (frequency - minCount));

			popularity = 1 + (popularity * multiplier);

			return popularity;
	}
	
	public boolean isCloudWithCount() {
		if (_frequenciesVisible && _displayStyle.equals("cloud")) {
			return true;
		}

		return false;
	}
	
	private List<TermCollector> getTermsCollectors() {
		FacetCollector facetCollector = _facet.getFacetCollector();

		List<TermCollector> termCollectors =
			Collections.<TermCollector>emptyList();

		if (facetCollector != null) {
			termCollectors = facetCollector.getTermCollectors();
		}
		
		return termCollectors;
	}
	
	public List<AssetTagsSearchFacetTermDisplayContext> buildTermDisplayContexts() {

		List<TermCollector> termCollectors =
				getTermsCollectors();
	
		
		if (termCollectors.isEmpty()) {
			return getEmptySearchResultTermDisplayContexts();
		}
	
		List<AssetTagsSearchFacetTermDisplayContext>
			assetTagsSearchFacetTermDisplayContexts = new ArrayList<>(
				termCollectors.size());
	
		int maxCount = 1;
		int minCount = 1;
	
		if (isCloudWithCount()) {
	
			// The cloud style may not list tags in the order of frequency,
			// so keep looking through the results until we reach the maximum
			// number of terms or we run out of terms
	
			for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
				if ((_maxTerms > 0) && (j >= _maxTerms)) {
					break;
				}
	
				TermCollector termCollector = termCollectors.get(i);
	
				int frequency = termCollector.getFrequency();
	
				if ((_frequencyThreshold > 0) &&
					(_frequencyThreshold > frequency)) {
	
					j--;
	
					continue;
				}
	
				maxCount = Math.max(maxCount, frequency);
				minCount = Math.min(minCount, frequency);
				}
		}
	
		double multiplier = 1;
	
		if (maxCount != minCount) {
			multiplier = (double)5 / (maxCount - minCount);
		}
	
		for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
			if ((_maxTerms > 0) && (j >= _maxTerms)) {
				break;
			}
	
			TermCollector termCollector = termCollectors.get(i);
	
			int frequency = termCollector.getFrequency();
	
			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > frequency)) {
	
				j--;
	
				continue;
			}
	
			AssetTagsSearchFacetTermDisplayContext
				assetTagsSearchFacetTermDisplayContext = buildTermDisplayContext(
					termCollector, maxCount, minCount, multiplier);
	
			if (assetTagsSearchFacetTermDisplayContext != null) {
				assetTagsSearchFacetTermDisplayContexts.add(
					assetTagsSearchFacetTermDisplayContext);
			}
		}
	
		return assetTagsSearchFacetTermDisplayContexts;
	}
	
	protected AssetTagsSearchFacetTermDisplayContext buildTermDisplayContext(
			TermCollector termCollector, int maxCount, int minCount,
			double multiplier) {

			int frequency = termCollector.getFrequency();

			boolean selected = false;

			String value = termCollector.getTerm();

			if (_paramValue.equals(value)) {
				selected = true;
			}

			int popularity = (int)getPopularity(
				frequency, minCount, maxCount, multiplier);

			AssetTagsSearchFacetTermDisplayContext 
			assetTagsSearchFacetTermDisplayContext =
					new AssetTagsSearchFacetTermDisplayContext();
			
			assetTagsSearchFacetTermDisplayContext.setValue(value);
			assetTagsSearchFacetTermDisplayContext.setFrequency(frequency);
			assetTagsSearchFacetTermDisplayContext.setPopularity(popularity);
			assetTagsSearchFacetTermDisplayContext.setSelected(selected);
			assetTagsSearchFacetTermDisplayContext.setFrequencyVisible(_frequenciesVisible);
			
			return assetTagsSearchFacetTermDisplayContext;
		}
	
	protected List<AssetTagsSearchFacetTermDisplayContext> 
	getEmptySearchResultTermDisplayContexts() {

		if (Validator.isNull(_paramValue)) {
			return Collections.emptyList();
		}
	
		AssetTagsSearchFacetTermDisplayContext 
		assetTagsSearchFacetTermDisplayContext =
				new AssetTagsSearchFacetTermDisplayContext();

		assetTagsSearchFacetTermDisplayContext.setValue(_paramValue);
		assetTagsSearchFacetTermDisplayContext.setFrequency(0);
		assetTagsSearchFacetTermDisplayContext.setPopularity(0);
		assetTagsSearchFacetTermDisplayContext.setSelected(true);
		assetTagsSearchFacetTermDisplayContext.setFrequencyVisible(_frequenciesVisible);
	
		return Collections.singletonList(
		assetTagsSearchFacetTermDisplayContext);
	}
	
	public String getDisplayStyle() {
		return _displayStyle;
	}
	
	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}
	
	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_frequenciesVisible = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setParamName(String paramName) {
		_paramName = paramName;
	}

	public void setParamValue(String paramValue) {
		_paramValue = paramValue;
	}


	private String _displayStyle;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _paramName;
	private String _paramValue;
	
}
