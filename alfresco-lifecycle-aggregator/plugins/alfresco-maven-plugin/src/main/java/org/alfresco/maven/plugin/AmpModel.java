package org.alfresco.maven.plugin;

import java.util.Arrays;
import java.util.List;

/**
 * Class holding all common well know constants for the AMP packaging/extraction process
 * @author columbro
 *
 */
public class AmpModel {

    public static final String AMP_FOLDER_LIB = "lib";

    public static final List<String> EXTENSION_LIST = Arrays.asList(new String[] {"jar","ejb","ejb-client","test-jar"});
}
