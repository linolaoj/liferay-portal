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

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Lino Alves
 * @author André de Oliveira
*/
@PrepareOnlyThisForTest(
	{DDMStructureLocalServiceUtil.class, ResourceBundleUtil.class}
)
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor(
	{
		"com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl",
		"com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil"
	}
)
public class DDMIndexerImplTest {

	@Before
	public void setUp() throws Exception {
		ddmFixture.setUp();

		documentFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		ddmFixture.tearDown();

		documentFixture.tearDown();
	}

	@Test
	public void testTwoFormLocalesAndOneNonDefaultLocalizedValue()
		throws Exception {

		Locale defaultLocale = LocaleUtil.US;
		Locale translationLocale = LocaleUtil.JAPAN;

		Set<Locale> availableLocales = new HashSet<>(
			Arrays.asList(defaultLocale, translationLocale));

		DDMForm ddmForm = buildDDMForm(availableLocales, defaultLocale);

		String fieldName = "text1";
		String indexType = "text";

		DDMFormField ddmFormField = buildDDMFormField(fieldName, indexType);

		ddmForm.addDDMFormField(ddmFormField);

		String fieldValue = "新規作成";

		DDMFormFieldValue ddmFormFieldValue = buildDDMFormFieldValue(
			fieldName, translationLocale, fieldValue, defaultLocale);

		Document document = createDocument();

		DDMStructure ddmStructure = createStructure(ddmForm);

		DDMFormValues ddmFormValues = buildDDMFormValues(
			ddmForm, ddmFormFieldValue);

		ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);

		Map<String, String> map = _withSortableValues(
			new HashMap<String, String>() {
				{
					put("ddm__text__NNNNN__text1_ja_JP", fieldValue);
				}
			});

		FieldValuesAssert.assertFieldValues(
			_replaceKeys(
				"NNNNN", String.valueOf(ddmStructure.getStructureId()), map),
			"ddm__text", document, fieldValue);
	}

	protected DDMForm buildDDMForm(
		Set<Locale> availableLocales, Locale defaultLocale) {

		return DDMFormTestUtil.createDDMForm(availableLocales, defaultLocale);
	}

	protected DDMFormField buildDDMFormField(
		String fieldName, String indexType) {

		String name = fieldName;
		String label = fieldName;
		String type = DDMFormFieldType.TEXT;
		String dataType = "string";
		boolean localizable = false;
		boolean repeatable = false;
		boolean required = true;

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			name, label, type, dataType, localizable, repeatable, required);

		ddmFormField.setIndexType(indexType);

		return ddmFormField;
	}

	protected DDMFormFieldValue buildDDMFormFieldValue(
		String name, Locale locale, String value, Locale defaultLocale) {

		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		localizedValue.addString(locale, value);

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			name, localizedValue);
	}

	protected DDMFormValues buildDDMFormValues(
		DDMForm ddmForm, DDMFormFieldValue... ddmFormFieldValues) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	protected Document createDocument() {
		return DocumentFixture.newDocument(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			DDMForm.class.getName());
	}

	protected DDMIndexer createIndexer() {
		DDMIndexerImpl ddmIndexerImpl = new DDMIndexerImpl();

		ddmIndexerImpl.setDDMFormValuesToFieldsConverter(
			new DDMFormValuesToFieldsConverterImpl());

		return ddmIndexerImpl;
	}

	protected DDMStructure createStructure(DDMForm ddmForm) {
		ddmStructureBuilder.setDDMForm(ddmForm);

		DDMStructure ddmStructure = ddmStructureBuilder.build();

		ddmFixture.whenFieldGetDDMStructure(ddmStructure);

		return ddmStructure;
	}

	protected final DDMFixture ddmFixture = new DDMFixture();
	protected final DDMIndexer ddmIndexer = createIndexer();
	protected final DDMStructureBuilder ddmStructureBuilder =
		new DDMStructureBuilder();
	protected final DocumentFixture documentFixture = new DocumentFixture();

	private static Map<String, String> _replaceKeys(
		String oldSub, String newSub, Map<String, String> map) {

		Set<Entry<String, String>> entrySet = map.entrySet();

		Stream<Entry<String, String>> entries = entrySet.stream();

		return entries.collect(
			Collectors.toMap(
				entry -> StringUtil.replace(entry.getKey(), oldSub, newSub),
				Map.Entry::getValue));
	}

	private static Map<String, String> _withSortableValues(
		Map<String, String> map) {

		Set<Entry<String, String>> entrySet = map.entrySet();

		Stream<Entry<String, String>> entries = entrySet.stream();

		Map<String, String> map2 = entries.collect(
			Collectors.toMap(
				entry -> entry.getKey() + "_sortable",
				entry -> StringUtil.toLowerCase(entry.getValue())));

		map2.putAll(map);

		return map2;
	}

}