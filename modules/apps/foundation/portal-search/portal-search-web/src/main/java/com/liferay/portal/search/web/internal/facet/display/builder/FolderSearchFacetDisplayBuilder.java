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

package com.liferay.portal.search.web.internal.facet.display.builder;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lino Alves
 */
public class FolderSearchFacetDisplayBuilder {

	public FolderSearchFacetDisplayContext build() {
		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			new FolderSearchFacetDisplayContext();

		folderSearchFacetDisplayContext.setParamName(_paramName);
		folderSearchFacetDisplayContext.setParamValue(_paramValue);

		_folderId = getFolderId(_paramValue);

		boolean nothingSelected = false;

		if (Validator.isBlank(_paramValue)) {
			nothingSelected = true;
		}

		folderSearchFacetDisplayContext.setNothingSelected(nothingSelected);

		List<TermCollector> termCollectors = getTermsCollectors();

		if (nothingSelected && termCollectors.isEmpty()) {
			folderSearchFacetDisplayContext.setRenderNothing(true);
		}

		folderSearchFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts());

		return folderSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFolderTitleLookup(FolderTitleLookup folderTitleLookup) {
		_folderTitleLookup = folderTitleLookup;
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

	protected FolderSearchFacetTermDisplayContext buildTermDisplayContext(
		TermCollector termCollector) {

		long curFolderId = GetterUtil.getLong(termCollector.getTerm());

		if (curFolderId == 0) {
			return null;
		}

		String title = _folderTitleLookup.getFolderTitle(curFolderId);

		if (title == null) {
			title = SearchStringUtil.concat("[", curFolderId, "]");
		}

		int frequency = termCollector.getFrequency();

		boolean selected = false;

		if ((_folderId != null) && _folderId.equals(curFolderId)) {
			selected = true;
		}

		FolderSearchFacetTermDisplayContext
			folderSearchFacetTermDisplayContext =
				new FolderSearchFacetTermDisplayContext();

		folderSearchFacetTermDisplayContext.setFolderId(curFolderId);
		folderSearchFacetTermDisplayContext.setDisplayName(title);
		folderSearchFacetTermDisplayContext.setSelected(selected);
		folderSearchFacetTermDisplayContext.setFrequency(frequency);
		folderSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);

		return folderSearchFacetTermDisplayContext;
	}

	protected List<FolderSearchFacetTermDisplayContext>
		buildTermDisplayContexts() {

		List<TermCollector> termCollectors = getTermsCollectors();

		if (termCollectors.isEmpty()) {
			return getEmptySearchResultTermDisplayContexts();
		}

		List<FolderSearchFacetTermDisplayContext>
			folderSearchFacetTermDisplayContexts = new ArrayList<>(
				termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			if ((_maxTerms > 0) && (i >= _maxTerms)) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > termCollector.getFrequency())) {

				break;
			}

			FolderSearchFacetTermDisplayContext
				folderSearchFacetTermDisplayContext = buildTermDisplayContext(
					termCollector);

			if (folderSearchFacetTermDisplayContext != null) {
				folderSearchFacetTermDisplayContexts.add(
					folderSearchFacetTermDisplayContext);
			}
		}

		return folderSearchFacetTermDisplayContexts;
	}

	protected List<FolderSearchFacetTermDisplayContext>
		getEmptySearchResultTermDisplayContexts() {

		if (_folderId == null) {
			return Collections.emptyList();
		}

		String title = _folderTitleLookup.getFolderTitle(_folderId);

		if (title == null) {
			return Collections.emptyList();
		}

		FolderSearchFacetTermDisplayContext
			folderSearchFacetTermDisplayContext =
				new FolderSearchFacetTermDisplayContext();

		folderSearchFacetTermDisplayContext.setDisplayName(title);
		folderSearchFacetTermDisplayContext.setFolderId(_folderId);
		folderSearchFacetTermDisplayContext.setSelected(true);
		folderSearchFacetTermDisplayContext.setFrequency(0);
		folderSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);

		return Collections.singletonList(folderSearchFacetTermDisplayContext);
	}

	protected Long getFolderId(String fieldParam) {
		long folderId = GetterUtil.getLong(fieldParam);

		if (folderId == 0) {
			return null;
		}

		return folderId;
	}

	protected List<TermCollector> getTermsCollectors() {
		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return Collections.emptyList();
		}

		return facetCollector.getTermCollectors();
	}

	private Facet _facet;
	private Long _folderId;
	private FolderTitleLookup _folderTitleLookup;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _paramName;
	private String _paramValue;

}