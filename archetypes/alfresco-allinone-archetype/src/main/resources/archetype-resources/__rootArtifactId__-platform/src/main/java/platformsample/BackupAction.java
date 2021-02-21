#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ${package}.platformsample;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A basic component that will create a backup of a content.
 * Only choose the extension for the backup in your 
 * alfresco-global.properties
 *
 * @author Luca Stancapiano
 */
public class BackupAction extends ActionExecuterAbstractBase {

	public static String DOCUMENT_NAME = "documentName";

	@Autowired
	private FileFolderService fileFolderService;

	private String extension;

	@Override
	public void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		String documentName = (String) action.getParameterValue(DOCUMENT_NAME);
		fileFolderService.create(actionedUponNodeRef, documentName + "." + extension, ContentModel.TYPE_CONTENT);

	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		// TODO Auto-generated method stub

	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
