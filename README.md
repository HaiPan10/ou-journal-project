# ou-journal-project

## Before you running
Run the query from script.sql to add some nesscessary data.

## Register API
```
Test endpoint: localhost:8080/api/tests/register
The json: 
{
    "email": "testgmail@gmail.com",
    "password": "123456",
    "userName": "testacc",
    "user": {
        "firstName": "testFirstName",
        "lastName": "testLastName",
        "email": "testgmail@gmail.com"
    }
}
```
## Verify API
```
Test endpoint: localhost:8080/api/tests/verify/2?status=ACCEPTED
Test endpoint: localhost:8080/api/tests/verify/2?status=REJECTED
```

## Upload API
```
Before testing: create an account and verify it
Test endpoint: localhost:8080/api/tests/upload
Body: form-data
Key: file -> Value: (choose any docx pdf)
Key: title -> Value: test
```

## Endpoints
```
Accounts page: http://localhost:8080/admin/accounts
```

## Generate Token for Postman Testing
```
Test endpoint: localhost:8080/api/tests/generate-token
The json:
{
    "username": "admin",
    "password": "123456"
}
The header of each request testing by postman should have:
Authorization: Bearer <Token>
```