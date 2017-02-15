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

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ResourceBundle;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author André de Oliveira
 */
public class DDMFixture {

	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		setUpDDMFormFieldFactoryHelperGetLocalizedValue();
		setUpDDMFormFieldFactoryHelperGetResourceBundle();
		setUpDDMStructureImplGetFieldProperty();
		setUpFieldGetDDMStructure();
	}

	public void tearDown() {
	}

	public void whenFieldGetDDMStructure(DDMStructure ddmStructure) {
		Mockito.doReturn(
			ddmStructure
		).when(
			_ddmStructureLocalService
		).fetchStructure(
			ddmStructure.getStructureId()
		);
	}

	protected void setUpBeanPropertiesUtil() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());
	}

	protected void setUpDDMFormFieldFactoryHelperGetLocalizedValue() {
		setUpLanguage();
	}

	protected void setUpDDMFormFieldFactoryHelperGetResourceBundle() {
		setUpPortalClassLoaderUtil();
		setUpResourceBundleUtil();
	}

	protected void setUpDDMStructureImplGetFieldProperty() {
		setUpBeanPropertiesUtil();
	}

	protected void setUpDDMStructureLocalServiceUtil() throws Exception {
		PowerMockito.spy(DDMStructureLocalServiceUtil.class);

		PowerMockito.doReturn(
			_ddmStructureLocalService
		).when(
			DDMStructureLocalServiceUtil.class, "getService"
		);
	}

	protected void setUpFieldGetDDMStructure() throws Exception {
		setUpDDMStructureLocalServiceUtil();
	}

	protected void setUpLanguage() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	protected void setUpPortalClassLoaderUtil() {
		PortalClassLoaderUtil.setClassLoader(_classLoader);
	}

	protected void setUpResourceBundleUtil() {
		PowerMockito.mockStatic(ResourceBundleUtil.class);

		PowerMockito.when(
			ResourceBundleUtil.getBundle(
				"content.Language", LocaleUtil.BRAZIL, _classLoader)
		).thenReturn(
			_resourceBundle
		);

		PowerMockito.when(
			ResourceBundleUtil.getBundle(
				"content.Language", LocaleUtil.US, _classLoader)
		).thenReturn(
			_resourceBundle
		);
	}

	@Mock
	private ClassLoader _classLoader;

	@Mock
	private DDMStructureLocalService _ddmStructureLocalService;

	@Mock
	private Language _language;

	@Mock
	private ResourceBundle _resourceBundle;

}