Nucleus Upload
================

This is the upload component for project nucleus 


TODO
----
* To avoid unwanted file uploads need to add custom file upload implementation. Using Vertx file upload, even file size exceeds (5MB) limit, file got uploaded in the disk. Need to revisit this file upload implementation. 

To understand build related stuff, take a look at **BUILD_README.md**.


How to do stuff
---------------

**Add new configuration**
* First add the key as constant in ConfigConstants.java
* Update the media-upload.json to provide a sample value
* Add file named "s3props.txt", this file should have s3 accesskey, secret and bucket name. 

* sample s3props file.

	s3.content.bucket.name=[contentbucketname]
	s3.user.bucket.name=[userbucketname]
	s3.access.key=[accesskey]
	s3.secret=[secret]






