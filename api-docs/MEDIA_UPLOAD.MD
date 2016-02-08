Features 
------------

	- Upload files less than 5 MB size
	- Move uploaded file to S3 for given file id and entity info
	
	
###Upload file

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus/{version}/media/upload |
| Auth | Required |
| Authorization Header | Authorization: Token [Authenticated access token]
| Response Code | 201 Created |

**Authorization Token**
```
Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg==
```

**CURL snippet**

```posh
curl -i  -H "Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg=="  -X POST http://nucleus.gooru.org/api/nucleus/v1/media/upload -F file=@"/home/user/myfile.pdf" 
```

**Response Body**

```json
{"fileName":"27170bb1-19eb-45b1-bd18-839062c6887f.pdf"}
```

###Move file to s3 

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | PUT |
| End Point | /api/nucleus/{version}/media/upload/s3 |
| Auth | Required |
| Authorization Header | Authorization: Token [Authenticated access token]
| Response Code | 200 OK |


**Authorization Token**
```
Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg==
```


**CURL snippet**

```posh
curl -i  -H "Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg=="  -X PUT  http://nucleus.gooru.org/api/nucleus/v1/media/upload/s3 -d '{"fileId":"27170bb1-19eb-45b1-bd18-839062c6887f.pdf", "entityType":"content", "entityId":"12345"}' 
```

**Response Body**

```json
{"fileName":"12345_27170bb1-19eb-45b1-bd18-839062c6887f.pdf"}
```

	
	