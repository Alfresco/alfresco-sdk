---
Title: Switching Alfresco Content Services and Share versions
Added: v3.0.0
Last reviewed: 2019-01-16
---
# Switching Alfresco Content Services and Share versions

The latest version of the Alfresco SDK supports different versions for Alfresco Content Services and Alfresco Share. Since each product is no longer 
released under one common version number, ACS (i.e. alfresco.war) and the Share UI (share.war) are now released with individual version numbers.

By default, SDK 4.0 is configured to generate projects using the most recent version of ACS and Share. You can easily change one (or both) versions by 
simply updating the `pom.xml` file in your project. The compatibility of these versions is up to you, however you should check in advance the right versions 
to use.

When editing `pom.xml` you will see a number of properties that define the Alfresco Content Services platform version and the Alfresco Share version, such as:

```
<alfresco.platform.version>6.0.7-ga</alfresco.platform.version>
<alfresco.share.version>6.0.c</alfresco.share.version>
```

Before continuing, always remember to start from a newly generated SDK project before changing the version numbers. We do not recommend changing the versions 
using developed customizations or source code.

This article is focused on the Community version. If you want to switch to Alfresco Enterprise, please visit [Working with Enterprise](enterprise.md).

The supported versions are explained in the next sections of this article.

## Switch to Alfresco version 6.0.x

Starting from a newly created Alfresco SDK 4.0 project (All-In-One, Platform JAR, or Share JAR), let’s replace the two properties with the following ones.

1. Open the pom.xml in your generated project.

2. Replace the properties with the following:

```
<alfresco.platform.version>6.0.7-ga</alfresco.platform.version>
<alfresco.share.version>6.0.c</alfresco.share.version>
```

In this example we have shown the switch to version 6.0.7-ga. Feel free to use the correct version for your project, paying attention to the compatible versions 
of Alfresco Content Services and Alfresco Share.

3. After changing the versions, delete all the previous data of your development Docker environment:

```
$ ./run.sh purge
```

4. Rebuild and restart the project:

```
$ ./run.sh build_start
```

**IMPORTANT:** Alfresco 6.1 is ready to work with JDK 11, but Alfresco 6.0 needs to be compiled and run using JDK 8, so please take this into account when you
switch from version 6.1.x to 6.0.x. If you compile Alfresco 6.0.x with JDK 11 you'll experience the issue described in the [Troubleshooting page](troubleshooting.md) 
about wrong JDK versions.
