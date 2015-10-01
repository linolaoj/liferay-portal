package com.liferay.dynamic.data.lists.form.web.lar;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.liferay.dynamic.data.lists.form.web.constants.DDLFormPortletKeys;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.portlet.exportimport.lar.BasePortletDataHandler;
import com.liferay.portlet.exportimport.lar.PortletDataHandler;
import com.liferay.portlet.exportimport.xstream.XStreamAliasRegistryUtil;

@Component(
		property = {"javax.portlet.name=" + DDLFormPortletKeys.DYNAMIC_DATA_LISTS_FORM},
		service = PortletDataHandler.class
	)
public class DDLFormPortletDataHandler extends BasePortletDataHandler {

		public static final String NAMESPACE = "dynamic_data_lists_form";

		@Activate
		protected void activate() {
			setDataLocalized(true);

			XStreamAliasRegistryUtil.register(
					DDLRecordImpl.class, "DDLRecord");
			XStreamAliasRegistryUtil.register(
				DDLRecordSetImpl.class, "DDLRecordSet");
			XStreamAliasRegistryUtil.register(
				DDMStructureImpl.class, "DDMStructure");
			XStreamAliasRegistryUtil.register(
					DDMTemplateImpl.class, "DDMTemplate");
		}
	
	
}
