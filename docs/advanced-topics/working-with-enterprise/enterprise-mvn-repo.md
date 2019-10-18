---
Title: How to configure private Alfresco Nexus repository
Added: v3.0.0
Last reviewed: 2019-10-18
---
# How to configure private Alfresco Nexus repository

The first matter to consider is to ensure that you have credentials for the Alfresco Private Repository ([artifacts.alfresco.com](artifacts.alfresco.com)), where the Alfresco artifacts are stored. Enterprise customers and partners can 
request these credentials opening a ticket on the [Alfresco Support Portal](http://support.alfresco.com). 

Once you have suitable credentials, you need to add support for Alfresco private Maven repository to your configuration. This would typically be done by 
adding your access credentials to the `settings.xml` contained in your `~/.m2` directory (for Linux and OS X). On Windows this resolves to 
`C:\Users\<username>\.m2`.

To do this, load `settings.xml` into your editor and add the following new server configuration in the `<servers>` section:

```
<server>
    <id>alfresco-private-repository</id>
    <username>username</username>
    <password>password</password>
</server>
```

You will need to replace the placeholder text with your real username and password as allocated by Alfresco. The id value should not be changed as it 
is used in the Alfresco SDK project build files to specify the Enterprise artifacts Maven repository.

It is possible to use encrypted passwords here. See the [official Maven documentation](http://maven.apache.org/guides/mini/guide-encryption.html) for details 
on how to do this.

At this point you have configured Maven to have access to the Alfresco Private Repository.
