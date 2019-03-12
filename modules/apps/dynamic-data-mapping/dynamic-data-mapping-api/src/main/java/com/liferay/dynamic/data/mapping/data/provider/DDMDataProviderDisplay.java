package com.liferay.dynamic.data.mapping.data.provider;

import java.util.List;

import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;

import aQute.bnd.annotation.ProviderType;

@ProviderType
public interface DDMDataProviderDisplay {

	List<DDMDisplayTabItem> getTabItems();
	
	public String getPortletId();

	public DDMDisplayTabItem getDefaultTabItem();
	
}
