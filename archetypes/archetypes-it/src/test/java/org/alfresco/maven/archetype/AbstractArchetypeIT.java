package org.alfresco.maven.archetype;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Exposes the core functionality required to generate a new project from an archetype and execute some of the goals of the runner scripts.
 *
 * @author Jose Luis Osorno <joseluis.osorno@ixxus.com>
 */
public abstract class AbstractArchetypeIT {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArchetypeIT.class);

    protected static final File ROOT = new File("target/test-classes/");
    protected static final String LOG_FILENAME = "log.txt";
    protected static final String ERROR_FILENAME = "error.txt";
    protected static final String LINUX_EXEC = "run.sh";
    protected static final String WINDOWS_EXEC = "run.bat";

    protected ArchetypeProperties archetypeProperties;
    protected String projectPath;

    @Before
    public void setUp() throws VerificationException, IOException {
        archetypeProperties = createArchetypeProperties();
        projectPath = ROOT.getAbsolutePath() + File.separator + archetypeProperties.getProjectArtifactId();

        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        // Deleting a former created artifact from the archetype to be tested
        verifier.deleteArtifact(archetypeProperties.getProjectGroupId(), archetypeProperties.getProjectArtifactId(), archetypeProperties.getProjectVersion(),
                null);
        // Delete the created maven project
        verifier.deleteDirectory(archetypeProperties.getProjectArtifactId());
    }

    /**
     * Create the {@link ArchetypeProperties} object with the details of the archetype to use and the project to generate in the test.
     *
     * @return the {@link ArchetypeProperties} object with the details of the archetype to use and the project to generate in the test
     */
    protected abstract ArchetypeProperties createArchetypeProperties();

    /**
     * Generate a new project from an archetype and verify the generation was successful.
     */
    protected void generateProjectFromArchetype(final Logger logger) throws Exception {
        LOG.info("---------------------------------------------------------------------");
        LOG.info("Generating a new project from the archetype {}:{}:{}", archetypeProperties.getArchetypeGroupId(), archetypeProperties.getArchetypeArtifactId(),
                archetypeProperties.getArchetypeVersion());
        LOG.info("---------------------------------------------------------------------");
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.setSystemProperties(archetypeProperties.getSystemProperties());
        verifier.setAutoclean(false);
        verifier.executeGoal("archetype:generate");
        printVerifierLog("PROJECT GENERATION", verifier, logger);
        verifier.verifyErrorFreeLog();
    }

    /**
     * Generate a {@link ProcessBuilder} with the project runner script to execute an specific goal.
     *
     * @param goalToExecute the goal to be executed in the {@link ProcessBuilder}
     * @return the generated {@link ProcessBuilder}
     */
    protected ProcessBuilder getProcessBuilder(final String goalToExecute) {
        ProcessBuilder pb = new ProcessBuilder(getCommand(), goalToExecute);
        LOG.trace("ProcessBuilder environment: {}", pb.environment().toString());
        pb.directory(new File(projectPath));
        pb.redirectOutput(new File(projectPath + File.separator + LOG_FILENAME));
        pb.redirectError(new File(projectPath + File.separator + ERROR_FILENAME));
        return pb;
    }

    /**
     * Print the content of the log file of a {@link Verifier} into a specific {@link Logger}.
     *
     * @param prefix the prefix to print before each line
     * @param verifier the {@link Verifier} to print its log file content
     * @param logger the {@link Logger} to print the log content
     */
    protected void printVerifierLog(final String prefix, final Verifier verifier, final Logger logger) throws Exception {
        logger.info("[{}] - {}", prefix, "Standard output");
        verifier.loadFile(verifier.getBasedir(), verifier.getLogFileName(), false).forEach(line -> logger.info("[{}] - {}", prefix, line));
        logger.info("[{}] - {}", prefix, "Error output");
        verifier.loadFile(verifier.getBasedir(), ERROR_FILENAME, false).forEach(line -> logger.info("[{}] - {}", prefix, line));
    }

    private String getCommand() {
        return projectPath + File.separator + (System.getProperty("os.name").startsWith("Windows") ? WINDOWS_EXEC : LINUX_EXEC);
    }
}
