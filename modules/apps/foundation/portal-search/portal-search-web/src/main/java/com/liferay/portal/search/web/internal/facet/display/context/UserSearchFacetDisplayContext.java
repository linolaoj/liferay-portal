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

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lino Alves
 */
public class UserSearchFacetDisplayContext {

	public UserSearchFacetDisplayContext(
		Facet facet, String fieldParam, int frequencyThreshold, int maxTerms,
		boolean showFrequencies) {

			_facet = facet;
			_fieldParam = fieldParam;
			_frequencyThreshold = frequencyThreshold;
			_maxTerms = maxTerms;
			_showFrequencies = showFrequencies;
			_userName = fieldParam;
		}

		public String getFieldParamInputName() {
			return _facet.getFieldId();
		}

		public String getFieldParamInputValue() {
			return _fieldParam;
		}

		public List<UserSearchFacetTermDisplayContext>
		getTermDisplayContexts() {

			FacetCollector facetCollector = _facet.getFacetCollector();

			List<TermCollector> termCollectors =
				facetCollector.getTermCollectors();

			if (termCollectors.isEmpty()) {
				return getEmptySearchResultTermDisplayContexts();
			}

			List<UserSearchFacetTermDisplayContext>
				userSearchFacetTermDisplayContexts = new ArrayList<>(
					termCollectors.size());

			int limit = termCollectors.size();

			if ((_maxTerms > 0) && (limit > _maxTerms)) {
				limit = _maxTerms;
			}

			for (int i = 0; i < limit; i++) {
				TermCollector termCollector = termCollectors.get(i);

				String userName = GetterUtil.getString(termCollector.getTerm());

				UserSearchFacetTermDisplayContext
					userSearchFacetTermDisplayContext = getTermDisplayContext(
						userName, termCollector);

				if (userSearchFacetTermDisplayContext != null) {
					userSearchFacetTermDisplayContexts.add(
						userSearchFacetTermDisplayContext);
				}

				if ((_frequencyThreshold > 0) &&
					(_frequencyThreshold > termCollector.getFrequency())) {

					break;
				}
			}

			return userSearchFacetTermDisplayContexts;
		}

		public boolean isNothingSelected() {
			if (_fieldParam.equals("")) {
				return true;
			}

			return false;
		}

		public boolean isRenderNothing() {
			if (Validator.isNotNull(_userName)) {
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

		protected List<UserSearchFacetTermDisplayContext>
			getEmptySearchResultTermDisplayContexts() {

			UserSearchFacetTermDisplayContext
				userSearchFacetTermDisplayContext = getTermDisplayContext(
					_userName, 0, true);

			if (userSearchFacetTermDisplayContext == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(userSearchFacetTermDisplayContext);
		}

		protected UserSearchFacetTermDisplayContext getTermDisplayContext(
			String userName, int frequency, boolean selected) {

			if (Validator.isNull(userName)) {
				return null;
			}

			return new UserSearchFacetTermDisplayContext(
				userName, selected, frequency, _showFrequencies);
		}

		protected UserSearchFacetTermDisplayContext getTermDisplayContext(
			String userName, TermCollector termCollector) {

			int frequency = termCollector.getFrequency();

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > frequency)) {

				return null;
			}

			boolean selected = false;

			if (StringUtil.equalsIgnoreCase(_userName, userName)) {
				selected = true;
			}

			return getTermDisplayContext(userName, frequency, selected);
		}

		private final int _frequencyThreshold;
		private final Facet _facet;
		private final String _fieldParam;
		private final String _userName;
		private final int _maxTerms;
		private final boolean _showFrequencies;

}