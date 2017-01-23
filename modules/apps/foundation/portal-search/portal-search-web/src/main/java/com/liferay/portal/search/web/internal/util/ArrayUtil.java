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

package com.liferay.portal.search.web.internal.util;

/**
 * @author André de Oliveira
 */
public class ArrayUtil {

	public static long[] toLongArray(String[] array) {
		long[] newArray = new long[array.length];

		for (int i = 0; i < array.length; i++) {
			newArray[i] = Long.valueOf(array[i]);
		}

		return newArray;
	}

}