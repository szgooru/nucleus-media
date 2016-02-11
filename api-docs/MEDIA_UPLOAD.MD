Features 
------------

	- Upload files less than 5 MB size
	
	
###Upload file

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus-media/{version}/uploads |
| Auth | Required |
| Authorization Header | Authorization: Token [Authenticated access token]
| Response Code | 201 Created |


**Authorization Token**
```
Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg==
```

**Response Body**

```json
{"file_name":"27170bb1-19eb-45b1-bd18-839062c6887f.pdf"}
```


**CURL snippet**

```posh
curl -i  -H "Authorization: Token YjllOTI0NjEtMzFjZC00ZGYxLWEyN2MtNzNjMjZkODI3NmVmOk1vbiBKYW4gMjUgMTM6MTc6MTAgSVNUIDIwMTY6MTQ1MzcwODAzMDAwNg=="  -X POST http://nucleus.gooru.org/api/nucleus-media/v1/uploads -F file=@"/home/user/myfile.pdf" 
```

