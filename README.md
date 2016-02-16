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
 
 - ```s3-config.properties``` copy  s3 configuaration template  file  from this repo  to  the cofiguration folder, this file should have s3 accesskey, secret and bucket name details. Replace the placeholder values in the copied property file with the actually values.
 





