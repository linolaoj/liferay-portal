package com.liferay.portal.search.web.startup;


import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;


@Component(immediate = true)
public class SearchPageStarter {

	@Activate
	public void activate(BundleContext bundleContext) {
		boolean privateLayout = false;
		Locale locale = Locale.getDefault();
		TimeZone timeZone = TimeZone.getDefault();
		
		final String groupName = GroupConstants.GUEST;
		final long companyId = PortalUtil.getDefaultCompanyId();
		
		String friendlyURL = "/pag-b";
		
		try {
			Group guestGroup = _groupLocalService.getGroup(companyId, groupName);
			final long guestGroupId = guestGroup.getGroupId();

			long userId = _userLocalService.getDefaultUserId(companyId);
			
			long groupId = guestGroupId;

			Layout layout = 
					LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
							groupId, privateLayout, friendlyURL);
			
			if(Validator.isNotNull(layout)) {
				return;
			}
			
			Map<String, Serializable> importLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactory.
					buildImportLayoutSettingsMap(
							userId, groupId, privateLayout, null,
						new HashMap<String,String[]>(), locale,
						timeZone);
			
			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.				
					addExportImportConfiguration(
						userId, groupId, "search-import", StringPool.BLANK, 
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT, 
						importLayoutSettingsMap,
						WorkflowConstants.STATUS_DRAFT, new ServiceContext());

			URL url = bundleContext.getBundle().getResource(
				"com/liferay/portal/search/web/startup/Public_Pages-20161229195818058.lar");

			_exportImportLocalService.importLayouts(
					exportImportConfiguration, url.openConnection().getInputStream());
			
		} 
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}
	
	@Reference
	private ExportImportConfigurationLocalService _exportImportConfigurationLocalService;
	@Reference
	private ExportImportLocalService _exportImportLocalService;
	@Reference
	private GroupLocalService _groupLocalService;
	@Reference
	private UserLocalService _userLocalService;
	
}
