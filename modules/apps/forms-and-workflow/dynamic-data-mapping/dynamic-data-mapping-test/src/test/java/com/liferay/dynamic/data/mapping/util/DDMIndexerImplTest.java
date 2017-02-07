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

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormValuesToFieldsConverterImpl;
import com.liferay.dynamic.data.mapping.internal.util.DDMImpl;
import com.liferay.dynamic.data.mapping.internal.util.DDMIndexerImpl;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONDeserializerImpl;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONSerializerImpl;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.test.SearchTestUtil;
import com.liferay.portal.util.LocalizationImpl;
import com.liferay.portal.util.PropsValues;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Lino Alves
 */
@PowerMockIgnore("javax.xml.stream.*")
@PrepareForTest({LocaleUtil.class, PropsValues.class})
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor(
	{
		"com.liferay.portal.kernel.xml.SAXReaderUtil",
		"com.liferay.portal.util.PropsValues"
	}
)
public class DDMIndexerImplTest extends BaseDDMTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpBeanPropertiesUtil();
		setUpConfigurationFactoryUtil();
		setUpDDM();
		setUpDDMFormJSONDeserializer();
		setUpDDMFormJSONSerializer();
		setUpDDMFormValuesJSONDeserializer();
		setUpDDMFormValuesJSONSerializer();
		setUpDDMStructureLocalServiceUtil();
		setUpHtmlUtil();
		setUpJSONFactoryUtil();
		setUpLanguageUtil();
		setUpLocaleUtil();
		setUpLocalizationUtil();
		setUpPropsUtil();
		setUpPropsValues();
		setUpSAXReaderUtil();
	}

	@Test
	public void testIndexDDMFieldWithMultipleLocales() {
		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);

		DDMForm ddmForm = createDDMForm(availableLocales, LocaleUtil.US);

		DDMFormField textField = DDMFormTestUtil.createTextDDMFormField(
			"text1", false, false, true);

		textField.setIndexType("text");

		ddmForm.addDDMFormField(textField);

		String text1StringValue = RandomTestUtil.randomString();

		LocalizedValue text1LocalizedValue =
			DDMFormValuesTestUtil.createLocalizedValue(
				text1StringValue, LocaleUtil.US);

		DDMFormFieldValue textDDMFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"text1", text1LocalizedValue);

		DDMFormValues ddmFormValues = createDDMFormValues(
			ddmForm, textDDMFormFieldValue);

		DDMStructure structure = createStructure("Test Structure", ddmForm);

		Document document = SearchTestUtil.createDocument(
			DDMForm.class.getName());

		DDMIndexerImpl ddmIndexerImpl = new DDMIndexerTest();

		ddmIndexerImpl.addAttributes(document, structure, ddmFormValues);

		long structureId = structure.getStructureId();

		Assert.assertNotEquals(
			StringPool.BLANK,
			document.get(
				LocaleUtil.US, "ddm__text__" + structureId + "__text1_en_US"));

		Assert.assertNotEquals(
			StringPool.BLANK,
			document.get(
				LocaleUtil.BRAZIL,
				"ddm__text__" + structureId + "__text1_pt_BR"));

		Assert.assertEquals(
			StringPool.BLANK,
			document.get("ddm__text__" + structureId + "__text1_es_ES"));
	}

	protected DDMFormValues createDDMFormValues(
		DDMForm ddmForm, DDMFormFieldValue... ddmFormFieldValues) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	protected void setUpBeanPropertiesUtil() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());
	}

	protected void setUpDDM() throws Exception {
		java.lang.reflect.Field field = ReflectionUtil.getDeclaredField(
			DDMImpl.class, "_ddmFormJSONDeserializer");

		field.set(_ddm, ddmFormJSONDeserializer);

		field = ReflectionUtil.getDeclaredField(
			DDMImpl.class, "_ddmFormValuesJSONDeserializer");

		field.set(_ddm, _ddmFormValuesJSONDeserializer);

		field = ReflectionUtil.getDeclaredField(
			DDMImpl.class, "_ddmFormValuesJSONSerializer");

		field.set(_ddm, _ddmFormValuesJSONSerializer);
	}

	protected void setUpDDMFormValuesJSONDeserializer() throws Exception {
		java.lang.reflect.Field field = ReflectionUtil.getDeclaredField(
			DDMFormValuesJSONDeserializerImpl.class, "_jsonFactory");

		field.set(_ddmFormValuesJSONDeserializer, new JSONFactoryImpl());
	}

	protected void setUpDDMFormValuesJSONSerializer() throws Exception {
		java.lang.reflect.Field field = ReflectionUtil.getDeclaredField(
			DDMFormValuesJSONSerializerImpl.class, "_jsonFactory");

		field.set(_ddmFormValuesJSONSerializer, new JSONFactoryImpl());
	}

	protected void setUpLocalizationUtil() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	protected void setUpPropsUtil() throws Exception {
		PropsUtil.setProps(mock(Props.class));
	}

	protected class DDMFormValuesToFieldsConverterMock
		extends DDMFormValuesToFieldsConverterImpl {

		@Override
		public Fields convert(
				DDMStructure ddmStructure, DDMFormValues ddmFormValues)
			throws PortalException {

			long structureId = ddmStructure.getStructureId();

			Fields mockFields = new Fields();
			Fields fields = super.convert(ddmStructure, ddmFormValues);

			Iterator<Field> iterator = fields.iterator();

			while (iterator.hasNext()) {
				Field field = iterator.next();

				MockField mockField = new MockField(
					structureId, field.getName(), field.getValuesMap(),
					Locale.US);

				mockFields.put(mockField);
			}

			return mockFields;
		}

	}

	protected class DDMIndexerTest extends DDMIndexerImpl {

		protected DDMIndexerTest() {
			setDDMFormValuesToFieldsConverter(
				new DDMFormValuesToFieldsConverterMock());
		}

	}

	private final DDMImpl _ddm = new DDMImpl();
	private final DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer =
		new DDMFormValuesJSONDeserializerImpl();
	private final DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer =
		new DDMFormValuesJSONSerializerImpl();

}