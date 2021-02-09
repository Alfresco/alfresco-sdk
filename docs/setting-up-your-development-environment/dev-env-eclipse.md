---
Title: Setting up your development environment using Eclipse
Added: v3.0.0
Last reviewed: 2021-02-09
---
# Setting up your development environment using Eclipse

The Maven Alfresco SDK is designed to work well with Eclipse. This support includes the ability to import existing Alfresco projects created using the 
Alfresco SDK.

Here we assume you already have an Eclipse installation up and running, together with an available Alfresco project created using the Alfresco SDK. If you 
don't have a project already, follow the steps in [Getting started with Alfresco SDK 4.2](../getting-started.md) to learn how to quickly generate it in a few 
easy steps.

## Importing the Alfresco project into Eclipse

1. Starting from Eclipse, select `File > Import > Maven > Existing Maven Projects` from the main menu to import the Alfresco project.

![Eclipse maven project import](../docassets/images/sdk-dev-env-eclipse-import.png)

2. Click `Next` then browse to the root of the Alfresco project.

![Alt text](../docassets/images/sdk-dev-env-eclipse-project.png "Eclipse maven project selection")

3. Click `Finish` to start importing the project into Eclipse.

Before completing the import, Eclipse checks the completeness of the local Maven repository. If you already have a local repository that includes all the 
required dependencies, this task will finish relatively quickly. Otherwise, be patient and wait until the downloads are completed (it can take some time).

Once the import is complete, a warning message may be displayed.

![Alt text](../docassets/images/sdk-dev-env-eclipse-warning.png "Eclipse maven project import warning")

4. Click `Resolve All Later` to complete the import task.

5. Check the Markers tab in the bottom panel, where you may see some Maven problems. Expand the list and right click on a item with an error, then select 
`Quick Fix` and mark as shown.

![Alt text](../docassets/images/sdk-dev-env-eclipse-quickfix.png "Eclipse maven project import quick fix")

6. Click `Finish` to confirm the fix.

You may be asked to confirm your selection.

7. Repeat the fix for all similar issues you have. Note that these issues really depend on the archetype you used to generate the project.

Once done, you may see an error with description: Project configuration is not up-to-date with pom.xml.

8. To fix this, right click one of the Alfresco projects and select `Maven > Update Project`, ensure all the Alfresco projects and sub-projects are selected, 
and then click `OK`.

Once this is done, the project is successfully imported in Eclipse. 

If you want more detail about how to work with the project, please visit [Working with generated projects](../working-with-generated-projects/README.md).
