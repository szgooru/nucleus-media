Features 
------------

	- Upload files less than 5 MB size
	- Upload image files by the web URL 
	
	
###Upload file

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus-media/{version}/uploads |
| Auth | Required |
| Authorization Header | Authorization: Token [Authenticated access token]
| Response Code | 201 Created |


**Request parameters**

| Name | comments |
|------------------------|--------|
| entity_type | supported entity types are content and user |
| url | valid image web url with encoded |


**Authorization Token**
```
Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg==
```


**CURL snippet**

**Upload file from local disk**

```posh
curl -i  -H "Authorization: Token OTM2NjBiNTItNmZjNi00YmY5LTgyOGQtMzQzNGVhODdmZDAzOlNhdCBKYW4gMzAgMTU6NDM6MTMgSVNUIDIwMTY6MTQ1NDE0ODc5MzM0Ng=="   -X POST http://nucleus.gooru.org/api/nucleus-media/v1/uploads?entity_type=content -F file=@"travel_bag.jpg"
```


**Response Body**

```json
{"filename":"27170bb1-19eb-45b1-bd18-839062c6887f.jpg"}
```


**Upload file with web URL**

```posh
curl -i  -H "Authorization: Token OTM2NjBiNTItNmZjNi00YmY5LTgyOGQtMzQzNGVhODdmZDAzOlNhdCBKYW4gMzAgMTU6NDM6MTMgSVNUIDIwMTY6MTQ1NDE0ODc5MzM0Ng==" -d "entity_type=content&url=http://www.joomlaworks.net/images/demos/galleries/abstract/7.jpg"  -X POST http://nucleus.gooru.org/api/nucleus-media/v1/uploads
```


**Response Body**

```json
{"filename":"c5dacef5-cfd7-4438-9799-40c9039250d5.jpg"}
```
