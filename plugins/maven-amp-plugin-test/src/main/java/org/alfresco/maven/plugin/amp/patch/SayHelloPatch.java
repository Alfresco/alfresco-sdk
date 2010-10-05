package org.alfresco.maven.plugin.amp.patch;

import org.alfresco.repo.admin.patch.AbstractPatch;

public class SayHelloPatch extends AbstractPatch {

	@Override
	protected String applyInternal() throws Exception {
		return "Hello";
	}

}
