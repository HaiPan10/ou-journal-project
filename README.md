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
    "password": "123456",
    "role": "ROLE_ADMIN"
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

## Custom new AuthenticationUser extends userDetails.User
```
Including additional information:
Long id;
String firstName;
String lastName;
String email;
String userName;
```

## How To Get Authentication Information In Thymeleaf
```
Using the #authentication.principal of the Thymeleaf security, get from the AuthenticationUser
For example:
<div th:text="${#authentication.principal.id}">
<div th:text="${#authentication.principal.lastName}">
```

## How To Get Authentication Information In Controller Or RestController
```
Including the "@AuthenticationPrincipal AuthenticationUser currentUser" as the
parameters of the Controller or Restcontroller
```

## Re submit manuscript 
```
Test endpoint: GET:localhost:8080/api/tests/articles/re-submit/{articleId}
Body: form-data
Key: file -> input your file
Key: note -> input any author note
```

## Login from email 
```
Test endpoint: GET:localhost:8080/api/accounts/login?token={token}
This GET api will authenticate user by login with JWT Token as the request parameters.
After that, It'll redirect user to the target endpoint by response with status code 301 MOVED_PERMANENTLY.
To use this api simply call the JwtService method in order to generate a token:
public String generateArticleMailActionToken(User user, Article article, String roleName, String targetEndpoint);
And put this token into the api request paramaters (token).
Note:
- This api only work with a url included in a email sent to user.
- Must declare the roleName param otherwise this token is invalid.
- The targetEndpoint param can be null.
```