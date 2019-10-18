---
Title: How to configure private Alfresco Docker registry
Added: v3.0.0
Last reviewed: 2019-10-18
---
# How to configure private Alfresco Docker registry

In order to download the Docker images needed to work with Alfresco Content Services Enterprise Edition it is required to configure the Alfresco private Docker registry 
hosted at [Quay.io](https://quay.io/). 

The first matter to consider is to ensure that you have credentials for the Alfresco private Docker registry, where the Alfresco images are stored. Customers and partners can 
request these credentials opening a ticket on the [Alfresco Support Portal](http://support.alfresco.com). 

Once you have suitable credentials, you only need to login your docker installation to the Quay.io Docker registry:

```
$ docker login quay.io
```

At this point you have configured Docker to have access to the Alfresco private Docker registry at [Quay.io](https://quay.io/).
