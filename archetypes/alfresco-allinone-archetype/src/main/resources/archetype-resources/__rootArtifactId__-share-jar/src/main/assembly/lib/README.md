# 3rd party libraries (JARs) that should be included in WAR

Put here any 3rd party libraries (JARs) that are needed by customizations, 
but that are not part of the out-of-the-box libraries in the *share/WEB-INF/lib* 
directory. 

**Note**. Module dependency needs to be set to amp for the libs to be applied by MMT:

`
<moduleDependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>some-share-jar</artifactId>
    <version>${project.version}</version>
    <type>amp</type>
</moduleDependency>
`
  
**Important**. If you need to use a library that is available out-of-the-box, then
               include it as a *provided* dependency in the module.