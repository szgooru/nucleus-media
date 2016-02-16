Nucleus Upload
================

This is the upload component for project nucleus 


TODO
----
* To avoid unwanted file uploads need to add custom file upload implementation. Using Vertx file upload, even file size exceeds (5MB) limit, file got uploaded in the disk. Need to revisit this file upload implementation. 

To understand build related stuff, take a look at **BUILD_README.md**.


How to do stuff
---------------

**Configuration setup in nucleus-media.json**

 - Update the file storage path, json key is "upload.location" it should be NFS storage  file system mount path.
 
 - Create s3 configuaration file inside the cofiguration folder name as  "s3-config.properties", this file should have s3 accesskey, secret and bucket names detail. 





