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

package com.liferay.dynamic.data.lists.form.web.lar;

import com.liferay.dynamic.data.lists.form.web.constants.DDLFormPortletKeys;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.portlet.exportimport.lar.BasePortletDataHandler;
import com.liferay.portlet.exportimport.lar.PortletDataHandler;
import com.liferay.portlet.exportimport.xstream.XStreamAliasRegistryUtil;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author Lino Alves
 *
 */

@Component(
		property = {"javax.portlet.name=" + DDLFormPortletKeys.DYNAMIC_DATA_LISTS_FORM},
		service = PortletDataHandler.class
	)
public class DDLFormPortletDataHandler extends BasePortletDataHandler {

		public static final String NAMESPACE = "dynamic_data_lists_form";

		@Activate
		protected void activate() {
			setDataLocalized(true);

			XStreamAliasRegistryUtil.register(DDLRecordImpl.class, "DDLRecord");
			XStreamAliasRegistryUtil.register(
				DDLRecordSetImpl.class, "DDLRecordSet");
			XStreamAliasRegistryUtil.register(
				DDMStructureImpl.class, "DDMStructure");
			XStreamAliasRegistryUtil.register(
				DDMTemplateImpl.class, "DDMTemplate");
		}

}