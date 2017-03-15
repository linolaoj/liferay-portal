package com.liferay.portal.search.web.internal.result.display.builder;

import java.util.Locale;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.BaseSearchEngine;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.search.test.SearchTestUtil;
import com.liferay.portal.search.test.TestIndexerRegistry;
import com.liferay.portal.search.web.constants.SearchPortletParameterNames;
import com.liferay.portal.search.web.internal.display.context.IndexSearchPropsValues;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchDisplayContext;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portlet.documentlibrary.util.DLFileEntryIndexer;
import com.liferay.portlet.messageboards.util.MBMessageIndexer;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

@PrepareForTest(AssetRendererFactoryRegistryUtil.class)
public class SearchResultSummaryDisplayBuilderTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		_themeDisplay = createThemeDisplay();

		setUpPropsUtil();

		setUpFacetedSearcherManager();
		setUpHttpServletRequest();
		setUpPortletURLFactory();
		
		setUpRegistryUtil();
		setUpIndexerRegistry();
		setUpSearchEngineHelperUtil();
		//setUpHttpUtil();
		//setUpPortletPreferencesFactoryUtil();
		
		setUpAssetEntryLocalService();
		setUpAssetRendererFactoryRegistryUtil();
		
		setUpRenderRequest();
	}
	
	protected void setUpHttpUtil() throws Exception {
		HttpUtil httpUtil = new HttpUtil();

		httpUtil.setHttp(_http);
	}


	protected void setUpRegistryUtil() throws Exception {
		Registry registry = new BasicRegistryImpl();

		RegistryUtil.setRegistry(registry);

		//registry.registerService(Indexer.class, new DLFileEntryIndexer());
		registry.registerService(Indexer.class, new MBMessageIndexer());
	}

	protected void setUpSearchEngineHelperUtil() {
		PowerMockito.mockStatic(SearchEngineHelperUtil.class, Mockito.CALLS_REAL_METHODS);

		PowerMockito.stub(
			PowerMockito.method(
				SearchEngineHelperUtil.class, "getDefaultSearchEngineId")
		).toReturn(
			SearchEngineHelper.SYSTEM_ENGINE_ID
		);

		PowerMockito.stub(
			PowerMockito.method(
				SearchEngineHelperUtil.class, "getEntryClassNames")
		).toReturn(
			new String[0]
		);

		PowerMockito.stub(
			PowerMockito.method(
				SearchEngineHelperUtil.class, "getSearchEngine", String.class)
		).toReturn(
			new BaseSearchEngine()
		);
	}
	
	protected void setUpIndexerRegistry() {
		Registry registry = RegistryUtil.getRegistry();

		registry.registerService(
			IndexerRegistry.class, new TestIndexerRegistry());
	}
	
	protected void setUpPropsUtil() throws Exception {
		Props props = PowerMockito.mock(Props.class);
		
		Mockito.doReturn(
				"yyyyMMddHHmmss"
			).when(
					props
			).get(
				PropsKeys.INDEX_DATE_FORMAT_PATTERN
			);
		
		PropsUtil.setProps(props);
	}

	protected void setUpAssetEntryLocalService() throws Exception {
		_assetEntryLocalService = PowerMockito.mock(AssetEntryLocalService.class);
	}
	
	protected void setUpAssetRendererFactoryRegistryUtil() throws Exception {
		PowerMockito.mockStatic(AssetRendererFactoryRegistryUtil.class, new CallsRealMethods());
	}
	
	@Test
	public void testSearchResultsUserId() throws Exception {
		
		
		RenderResponse renderResponse = PowerMockito.mock(RenderResponse.class);
		
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		
		String title1 = "first thread a1";
		String content1 = "some content";
		long entryClassPK1 = 33812;
		long userId1 = 20153;
		
		String title2 = "re: first thread a1";
		String content2 = "zzz";
		long entryClassPK2 = 33835;
		long userId2 = 33782;
		
		Document document1 = createMBMessageDocument(
				title1, content1, entryClassPK1, entryClassPK1, userId1);
		
		Document document2 = createMBMessageDocument(
				title2, content2, entryClassPK2, entryClassPK1, userId2);
		
		String requestKeywords = "zzz";

		SearchDisplayContext searchDisplayContext = createSearchDisplayContext(
				requestKeywords, renderRequest);
		
		SearchResultSummaryDisplayBuilder searchResultSummaryDisplayBuilder = 
			new SearchResultSummaryDisplayBuilder();

		searchResultSummaryDisplayBuilder.setAssetEntryLocalService(
			_assetEntryLocalService);
		searchResultSummaryDisplayBuilder.setCurrentURL("");
		searchResultSummaryDisplayBuilder.setDocument(document2);
		searchResultSummaryDisplayBuilder.setHighlightEnabled(
			searchDisplayContext.isHighlightEnabled());
		searchResultSummaryDisplayBuilder.setLanguage(
			new LanguageImpl());
		searchResultSummaryDisplayBuilder.setLocale(Locale.US);
		searchResultSummaryDisplayBuilder.setPortletURLFactory(
			searchDisplayContext.getPortletURLFactory());
		searchResultSummaryDisplayBuilder.setQueryTerms(
			searchDisplayContext.getQueryTerms());
		searchResultSummaryDisplayBuilder.setRenderRequest(renderRequest);
		searchResultSummaryDisplayBuilder.setRenderResponse(renderResponse);
		searchResultSummaryDisplayBuilder.setRequest(request);
		searchResultSummaryDisplayBuilder.setResourceActions(
			ResourceActionsUtil.getResourceActions());
		searchResultSummaryDisplayBuilder.setSearchResultPreferences(
			searchDisplayContext.getSearchResultPreferences());
		searchResultSummaryDisplayBuilder.setThemeDisplay(_themeDisplay);

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = 
			searchResultSummaryDisplayBuilder.build();
		
		searchResultSummaryDisplayContext.getAssetEntryUserId();
		
	}
	
	protected Document createMBMessageDocument(
		String title, String content, 
		long classPK, long rootClassPk, long userId) {
		
		Document document = SearchTestUtil.createDocument(
				_MB_MESSAGE_CLASS_NAME);
		
		document.addKeyword("entryClassPK", classPK);
		document.addKeyword("groupId", 20140);
		document.addKeyword("discussion", false);
		document.addKeyword("classNameId", 0);
		document.addKeyword("threadId", 33813);
		document.addKeyword("classPK", 0);
		document.addKeyword("scopeGroupId", 20140);
		document.addKeyword("visible", true);
		document.addKeyword("companyId", 20113);
		document.addKeyword("rootEntryClassPK", rootClassPk);
		document.addKeyword("status", 0);
		document.addText("title", title);
		document.addText("content", content);
		document.addKeyword(
			"uid", "com.liferay.message.boards.kernel.model.MBMessage_PORTLET_" + classPK);
		document.addText("localized_title_en_US_sortable", title);
		document.addKeyword(
			"entryClassName", "com.liferay.message.boards.kernel.model.MBMessage");
		document.addKeyword("userId", userId);
		document.addKeyword("userName", "zae sae");
		document.addText("localized_title", title);
		document.addText("snippet_content", content);
		document.addKeyword("categoryId", 0);
		
		return document;
		
	}
	
	protected JSONArray createJSONArray() {
		JSONArray jsonArray = Mockito.mock(JSONArray.class);

		Mockito.doReturn(
			1
		).when(
			jsonArray
		).length();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			jsonArray
		).getString(
			0
		);

		return jsonArray;
	}

	protected JSONFactory createJSONFactory() {
		JSONFactory jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.doReturn(
			createJSONObject()
		).when(
			jsonFactory
		).createJSONObject();

		return jsonFactory;
	}

	protected JSONObject createJSONObject() {
		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.doReturn(
			true
		).when(
			jsonObject
		).has(
			"values"
		);

		Mockito.doReturn(
			createJSONArray()
		).when(
			jsonObject
		).getJSONArray(
			"values"
		);

		return jsonObject;
	}

	protected IndexerRegistry createIndexRegistry(String className)
		throws Exception {

		IndexerRegistry indexerRegistry = Mockito.mock(IndexerRegistry.class);
		
		Mockito.doReturn(
			new MBMessageIndexer()
		).when(
			indexerRegistry
		).getIndexer(
			className
		);

		return indexerRegistry;
	}

	
	protected Portal createPortal(
			ThemeDisplay themeDisplay, RenderRequest renderRequest)
		throws Exception {

		Portal portal = Mockito.mock(Portal.class);

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getHttpServletRequest(
			renderRequest
		);

		return portal;
	}

	protected SearchDisplayContext createSearchDisplayContext(
			String keywords, RenderRequest renderRequest)
		throws Exception {

		setUpRequestKeywords(keywords);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(createJSONFactory());

		return new SearchDisplayContext(
			renderRequest, _portletPreferences,
			createPortal(_themeDisplay, renderRequest), Mockito.mock(Html.class),
			Mockito.mock(Language.class), facetedSearcherManager,
			Mockito.mock(IndexSearchPropsValues.class), portletURLFactory);
	}

	protected ThemeDisplay createThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(Mockito.mock(Company.class));
		themeDisplay.setUser(Mockito.mock(User.class));

		return themeDisplay;
	}
	
	protected void setUpFacetedSearcherManager() throws Exception {
		Mockito.doReturn(
			Mockito.mock(Hits.class)
		).when(
			facetedSearcher
		).search(
			Mockito.<SearchContext>any()
		);

		Mockito.doReturn(
			facetedSearcher
		).when(
			facetedSearcherManager
		).createFacetedSearcher();
	}

	protected void setUpHttpServletRequest() throws Exception {
		Mockito.doReturn(
			_themeDisplay
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);
	}

	protected void setUpPortletURLFactory() throws Exception {
		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			portletURLFactory
		).getPortletURL();
	}

	protected void setUpRenderRequest() throws Exception {
		renderRequest = PowerMockito.mock(RenderRequest.class);
		
		Mockito.doReturn(
			_themeDisplay
		).when(
			renderRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);
	}

	protected void setUpRequestKeywords(String keywords) {
		Mockito.doReturn(
			keywords
		).when(
			httpServletRequest
		).getParameter(
			SearchPortletParameterNames.KEYWORDS
		);

		Mockito.doReturn(
			keywords
		).when(
			renderRequest
		).getParameter(
			SearchPortletParameterNames.KEYWORDS
		);
	}
	
	@Mock
	protected FacetedSearcher facetedSearcher;

	@Mock
	protected FacetedSearcherManager facetedSearcherManager;

	@Mock
	protected HttpServletRequest httpServletRequest;

	@Mock
	protected PortletURLFactory portletURLFactory;

	@Mock
	protected RenderRequest renderRequest;
	
	@Mock
	protected AssetEntryLocalService _assetEntryLocalService;
	
	@Mock
	private Http _http;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();
	
	private final MockHttpServletRequest _mockOriginalServletRequest =
		new MockHttpServletRequest();

	@Mock
	private PortletPreferences _portletPreferences;

	private ThemeDisplay _themeDisplay = new ThemeDisplay();

	
	private final String _MB_MESSAGE_CLASS_NAME =
		MBMessage.class.getName();
	
}
