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

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lino Alves
 */
public class FolderSearchFacetDisplayContext {

	public FolderSearchFacetDisplayContext(
		Facet facet, String fieldParam, int countThreshold, int maxTerms,
		boolean showCounts, DLFolderLocalService dlFolderLocalService) {

			_facet = facet;
			_fieldParam = fieldParam;
			_countThreshold = countThreshold;
			_maxTerms = maxTerms;
			_showCounts = showCounts;
			_dlFolderLocalService = dlFolderLocalService;

			_folderId = GetterUtil.getLong(fieldParam);
		}

		public String getFieldParamInputName() {
			return _facet.getFieldId();
		}

		public String getFieldParamInputValue() {
			return _fieldParam;
		}

		public List<FolderSearchFacetTermDisplayContext>
		getTermDisplayContexts() {

			FacetCollector facetCollector = _facet.getFacetCollector();

			List<TermCollector> termCollectors =
				facetCollector.getTermCollectors();

			if (termCollectors.isEmpty()) {
				return getEmptySearchResultTermDisplayContexts();
			}

			List<FolderSearchFacetTermDisplayContext>
				folderSearchFacetTermDisplayContexts = new ArrayList<>(
					termCollectors.size());

			int limit = termCollectors.size();

			if ((_maxTerms > 0) && (limit > _maxTerms)) {
				limit = _maxTerms;
			}

			for (int i = 0; i < limit; i++) {
				TermCollector termCollector = termCollectors.get(i);

				long curFolderId = GetterUtil.getLong(termCollector.getTerm());

				if (curFolderId == 0) {
					continue;
				}

				FolderSearchFacetTermDisplayContext
					folderSearchFacetTermDisplayContext = getTermDisplayContext(
						curFolderId, termCollector);

				if (folderSearchFacetTermDisplayContext != null) {
					folderSearchFacetTermDisplayContexts.add(
						folderSearchFacetTermDisplayContext);
				}
			}

			return folderSearchFacetTermDisplayContexts;
		}

		public boolean isNothingSelected() {
			if (_fieldParam.equals("0")) {
				return true;
			}

			return false;
		}

		public boolean isRenderNothing() {
			if (_folderId != 0) {
				return false;
			}

			FacetCollector facetCollector = _facet.getFacetCollector();

			List<TermCollector> termCollectors =
				facetCollector.getTermCollectors();

			if (!termCollectors.isEmpty()) {
				return false;
			}

			return true;
		}

		protected List<FolderSearchFacetTermDisplayContext>
			getEmptySearchResultTermDisplayContexts() {

			FolderSearchFacetTermDisplayContext
				folderSearchFacetTermDisplayContext = getTermDisplayContext(
					_folderId, 0, true);

			if (folderSearchFacetTermDisplayContext == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(
				folderSearchFacetTermDisplayContext);
		}

		protected FolderSearchFacetTermDisplayContext getTermDisplayContext(
			long folderId, int count, boolean selected) {

			DLFolder folder = _dlFolderLocalService.fetchFolder(folderId);

			if (folder == null) {
				return null;
			}

			return new FolderSearchFacetTermDisplayContext(
				folder, selected, count, _showCounts);
		}

		protected FolderSearchFacetTermDisplayContext getTermDisplayContext(
			long folderId, TermCollector termCollector) {

			int count = termCollector.getFrequency();

			if ((_countThreshold > 0) && (_countThreshold > count)) {
				return null;
			}

			boolean selected = false;

			if (folderId == _folderId) {
				selected = true;
			}

			return getTermDisplayContext(folderId, count, selected);
		}

		private final int _countThreshold;
		private final DLFolderLocalService _dlFolderLocalService;
		private final Facet _facet;
		private final long _folderId;
		private final String _fieldParam;
		private final int _maxTerms;
		private final boolean _showCounts;

}