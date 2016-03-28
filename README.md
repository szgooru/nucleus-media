Nucleus Upload
================

This is the upload component for project nucleus 


TODO
----
* To avoid unwanted file uploads need to add custom file upload implementation. Using Vertx file upload, even file size exceeds (5MB) limit, file got uploaded in the disk. Need to revisit this file upload implementation. 
* Have to write job to clean up files(those are not uploaded to s3) which is exists in nfs repository 

To understand build related stuff, take a look at **BUILD_README.md**.


How to form thumbnail URL 
-------------------------
* On the file upload file name are generated in BE. File names are generated uuid with original file extension appended in the last.
* File uploade API only return the file name, from client side have to form the file url
* File upload API will return the file name, from the client side we have to use this file name and s3 bucket url to form the actual file url, see below example

         For example 
         
         s3 bucket url is - http://test-bucket01.s3.amazonaws.com 
         file name returned by API is - c3c35e8c-14e7-4345-97f2-a40c878d0344.png
         
         The final file url is - http://test-bucket01.s3.amazonaws.com/c3c35e8c-14e7-4345-97f2-a40c878d0344.png


How to do stuff
---------------

**Configuration setup in nucleus-media.json**

 - Update the file storage path, json key is "upload.location" it should be NFS storage  file system mount path.
 
 - Update the s3 configuration settings by replacing the placeholder values with the actual configuration values.
 





