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

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Lino Alves
 */
public class FolderSearchFacetTermDisplayContext {

	public FolderSearchFacetTermDisplayContext(
		DLFolder folder, boolean selected, int count, boolean showCount) {

			_folder = folder;
			_selected = selected;
			_count = count;
			_showCount = showCount;
		}

		public int getCount() {
			return _count;
		}

		public String getDescriptiveName() throws PortalException {

			return _folder.getName();
		}

		public long getFolderId() {
			return _folder.getFolderId();
		}

		public boolean isSelected() {
			return _selected;
		}

		public boolean isShowCount() {
			return _showCount;
		}

		private final int _count;
		private final DLFolder _folder;
		private final boolean _selected;
		private final boolean _showCount;

}