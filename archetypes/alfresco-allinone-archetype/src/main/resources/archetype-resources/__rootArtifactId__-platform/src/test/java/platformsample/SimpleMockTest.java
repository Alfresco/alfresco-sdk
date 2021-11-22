#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mock.test.AbstractForm;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ActionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

/**
 * Unit testing the mock system through the backup action
 *
 * @author Luca Stancapiano
 */
@RunWith(RemoteTestRunner.class)
@Remote(runnerClass = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-module-context.xml")
public class SimpleMockTest extends AbstractForm {

	@Autowired
	private BackupAction myAction;

	private String documentName = "VALID.pdf";

	@Before
	public void init() {
		super.init();

		// insert a document
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, documentName);
		properties.put(ContentModel.PROP_DESCRIPTION, documentName);
		String content = new String(com.adobe.xmp.impl.Base64.encode(documentName));
		insertDocument(workspace, documentName, content, properties);

		// verify the document is created
		ResultSet docs = serviceRegistry.getSearchService().query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
				SearchService.LANGUAGE_FTS_ALFRESCO, "PATH:\"/" + documentName + "\"");
		Assert.assertEquals("A document is created", 1, docs.length());
		String name = (String) serviceRegistry.getNodeService().getProperty(docs.getNodeRefs().get(0),
				ContentModel.PROP_NAME);
		Assert.assertTrue("VALID.pdf is created", name.equals(documentName));
	}

	@Test
	public void execute() {

		// execute the injected action
		Map<String, Serializable> parameterValues = new HashMap<String, Serializable>();
		parameterValues.put(BackupAction.DOCUMENT_NAME, documentName);
		Action action = new ActionImpl(null, null, null, parameterValues);
		myAction.executeImpl(action, workspace);

		// verify the document is created
		ResultSet docs = serviceRegistry.getSearchService().query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
				SearchService.LANGUAGE_FTS_ALFRESCO, "PATH:\"/" + documentName + ".bak\"");
		Assert.assertEquals("A backup document is created", 1, docs.length());
		String name = (String) serviceRegistry.getNodeService().getProperty(docs.getNodeRefs().get(0),
				ContentModel.PROP_NAME);
		Assert.assertTrue("VALID.pdf.bak is created", name.equals(documentName + ".bak"));

	}
}
