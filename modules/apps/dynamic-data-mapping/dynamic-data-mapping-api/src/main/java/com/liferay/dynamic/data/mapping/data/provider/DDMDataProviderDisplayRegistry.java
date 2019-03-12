package com.liferay.dynamic.data.mapping.data.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.liferay.portal.kernel.util.ListUtil;

import aQute.bnd.annotation.ProviderType;

@Component(immediate = true, service = DDMDataProviderDisplayRegistry.class)
@ProviderType
public class DDMDataProviderDisplayRegistry {
	
	public DDMDataProviderDisplay getDDMDataProviderDisplay(String portletId) {
		return _getDDMDisplay(portletId);
	}

	public List<DDMDataProviderDisplay> getDDMDisplays() {
		return _getDDMDisplays();
	}

	public String[] getPortletIds() {
		return _getPortletIds();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setDDMDisplay(DDMDataProviderDisplay ddmDataProviderDisplay) {
		_ddmDataProviderDisplay.put(ddmDataProviderDisplay.getPortletId(), ddmDataProviderDisplay);
	}

	protected void unsetDDMDisplay(DDMDataProviderDisplay ddmDataProviderDisplay) {
		_ddmDataProviderDisplay.remove(ddmDataProviderDisplay.getPortletId());
	}

	private DDMDataProviderDisplay _getDDMDisplay(String portletId) {
		return _ddmDataProviderDisplay.get(portletId);
	}

	private List<DDMDataProviderDisplay> _getDDMDisplays() {
		return ListUtil.fromMapValues(_ddmDataProviderDisplay);
	}

	private String[] _getPortletIds() {
		Set<String> portletIds = _ddmDataProviderDisplay.keySet();

		return portletIds.toArray(new String[portletIds.size()]);
	}

	private final Map<String, DDMDataProviderDisplay> _ddmDataProviderDisplay =
		new ConcurrentHashMap<>();
}
