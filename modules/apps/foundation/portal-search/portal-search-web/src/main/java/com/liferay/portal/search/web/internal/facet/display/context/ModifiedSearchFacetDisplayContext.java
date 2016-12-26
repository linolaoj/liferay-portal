package com.liferay.portal.search.web.internal.facet.display.context;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.ListUtil;

public class ModifiedSearchFacetDisplayContext implements Serializable {

	public List<ModifiedSearchFacetFieldDisplayContext>
	getModifiedSearchFacetFieldDisplayContexts() {
		return _modifiedSearchFacetFieldDisplayContext;
	}

	public String getFieldParamInputName() {
		return _facet.getFieldId();
	}

	public String getFieldParamInputValue() {
		return _fieldParam;
	}

	public Date getFromDate() {
		return _fromDate;
	}
	
	public Date getToDate() {
		return _toDate;
	}

	public boolean isRenderNothing() {
		return ListUtil.isEmpty(_modifiedSearchFacetFieldDisplayContext);
	}

	public void setModifiedSearchFacetFieldDisplayContexts(
			List<ModifiedSearchFacetFieldDisplayContext>
			modifiedSearchFacetFieldDisplayContext) {
				_modifiedSearchFacetFieldDisplayContext =
					modifiedSearchFacetFieldDisplayContext;
	}
	
	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFieldParamInputValue(String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFromDate(Date fromDate) {
		_fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		_toDate = toDate;
	}

	private List<ModifiedSearchFacetFieldDisplayContext>
	_modifiedSearchFacetFieldDisplayContext;
	private Date _fromDate;
	private Date _toDate;
	private Facet _facet;
	private String _fieldParam;
}
