create database ${alfresco.db.name};
grant all on ${alfresco.db.name}.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;
grant all on ${alfresco.db.name}.* to 'alfresco'@'localhost.localdomain' identified by 'alfresco' with grant option;