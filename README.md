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
Before testing: create 4 accounts and verify it
Test endpoint: localhost:8080/api/tests/upload
Body: form-data
Key: file 
Value: (choose any docx pdf)

Key: article
Notice: in authorArticles if user is not exists, pass lastName, firstName, email. If user is already exists, pass email or id
Value:
{
    "title": "my title", 
    "abstracts":"my abstract",
    "authorArticles": [
        {
            "user": {
                "id": 2
            },
            "authorRoles": [
                {
                    "authorType": {
                        "id": 1
                    }
                }
            ]
        },
        {
            "user": {
                "firstName": "Thành",
                "lastName": "Dương Hữu",
                "email": "thanhdh@gmail.com" 
            },
            "authorRoles": [
                {
                    "authorType": {
                        "id": 2
                    }
                }
            ]
        },
        {
            "user": {
                "firstName": "Hy",
                "lastName": "Thanh",
                "email": "thanhhy@gmail.com"
            },
            "authorRoles": [
                {
                    "authorType": {
                        "id": 3
                    }
                }
            ]
        }
    ]
}
Content type: application/json
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

## Generate Email Token
```
Test endpoint: GET:localhost:8080/api/tests/get-email-token/{accountId}"
This api will generate a test token email based account id.
```

## Reviewer Verify From Email With Existed Account
```
Test endpoint: GET:localhost:8080/api/accounts/reviewer/verify?token=<The Email Token>
With this API reviewer verify to join for review the article using existed account.

Calling getVerificationCodeFromToken(String token, SecrectType secrectType)
from JwtService for getting verification code from token

More methods references the JwtService.class
```

## Endpoints
```
Accounts page: http://localhost:8080/admin/accounts
Articles page: http://localhost:8080/admin/articles
```