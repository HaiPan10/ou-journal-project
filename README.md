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

## Endpoints
```
Accounts page: http://localhost:8080/admin/accounts
```