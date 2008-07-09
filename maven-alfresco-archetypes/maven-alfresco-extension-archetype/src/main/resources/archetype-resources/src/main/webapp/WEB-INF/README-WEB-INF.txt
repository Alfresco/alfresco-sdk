#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#    
#    http://www.apache.org/licenses/LICENSE-2.0
#    
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

Readme for the WEB-INF overlay procedures
-----------------------------------------
- Note:
This folder contents will be overlayed to the ${webapp.name}/WEB-INF folder. So for example a web.xml file put into this folder will overwrite (not "override" by classpath or inheritance rules) the existing web.xml.
This is useful for example for configuring external SSO authentication filters (e.g. NTLMAuthenticationFilter)
