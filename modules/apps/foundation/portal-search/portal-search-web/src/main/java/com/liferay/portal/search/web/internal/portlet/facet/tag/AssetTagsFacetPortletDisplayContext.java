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

package com.liferay.portal.search.web.internal.portlet.facet.tag;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Lino Alves
 */
public class AssetTagsFacetPortletDisplayContext {

	public static final String ATTRIBUTE =
		"AssetTagsFacetPortletDisplayContext";

	public String getFieldParamInputName() {
		return _fieldParamInputName;
	}

	public Collection<AssetTagsFacetPortletTermDisplayContext>
	getTermDisplayContexts() {

		return _termDisplayContexts;
	}

	public boolean isNothingSelected() {
		return _nothingSelected;
	}

	public boolean isRenderNothing() {
		return _renderNothing;
	}

	public void setFieldParamInputName(String fieldParamInputName) {
		_fieldParamInputName = fieldParamInputName;
	}

	public void setFieldParamInputValue(String fieldParamInputValue) {
		_fieldParamInputValue = fieldParamInputValue;
	}

	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}

	public void setRenderNothing(boolean renderNothing) {
		_renderNothing = renderNothing;
	}

	public void setTerms(
		Collection<AssetTagsFacetPortletTermDisplayContext>
		termDisplayContexts) {

		_termDisplayContexts = termDisplayContexts;
	}

	public String getFieldParamInputValue() {
		return _fieldParamInputValue;
	}

	private String _fieldParamInputName;
	private String _fieldParamInputValue;
	private boolean _nothingSelected;
	private boolean _renderNothing;
	private Collection<AssetTagsFacetPortletTermDisplayContext> _termDisplayContexts =
		Collections.emptyList();

}