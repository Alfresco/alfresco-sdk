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
   
Restore procedure:
------------------
(README: Only supported for WAR integrated build ATM)

1. Place here your 6 full repository export files (5 acp + 1 xml) calling the export 
package "export" (so that your file will appear like export_spaces.acp, export_users.acp, etc.) 

2. run your build with the property

mvn clean package -DrestoreVersion=testRestoreVersion 

3. deploy as a war (mvn jboss:deploy)

4. if you had a consistent repository/database you should have your repo fully imported