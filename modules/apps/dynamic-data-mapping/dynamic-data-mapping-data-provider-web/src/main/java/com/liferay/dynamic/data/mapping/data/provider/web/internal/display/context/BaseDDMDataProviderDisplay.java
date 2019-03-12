package com.liferay.dynamic.data.mapping.data.provider.web.internal.display.context;

import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderDisplay;
import com.liferay.dynamic.data.mapping.data.provider.web.internal.display.context.util.DDMDataProviderRequestHelper;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.util.BaseDDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import com.liferay.portal.kernel.language.LanguageUtil;

import aQute.bnd.annotation.ProviderType;

@ProviderType
public class BaseDDMDataProviderDisplay implements DDMDataProviderDisplay {

	@Override
	public DDMDisplayTabItem getDefaultTabItem() {
		return new DDMDisplayTabItem() {

			@Override
			public String getTitle(
				LiferayPortletRequest liferayPortletRequest,
				LiferayPortletResponse liferayPortletResponse) {

				String scopeTitle = ParamUtil.getString(
					liferayPortletRequest, "scopeTitle");

				if (Validator.isNull(scopeTitle)) {
					return LanguageUtil.get(
							liferayPortletRequest.getHttpServletRequest(),
							"data-providers");
				}

				return scopeTitle;
			}

		};
	}
	
	@Override
	public List<DDMDisplayTabItem> getTabItems() {
		return Arrays.asList(getDefaultTabItem());
	}
	
	@Override
	public String getPortletId() {
		return PortletProviderUtil.getPortletId(
			DDMDataProviderInstance.class.getName(),
			PortletProvider.Action.EDIT);
	}
	
}
