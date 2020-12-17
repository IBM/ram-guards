# A guide to use system partner mode in Ram-Guards

[RAM-GUARDS](https://github.ibm.com/Danube-Engine/RAM-GUARDS) supports two kinds of call. One is ibm real user call, the other is ibm system partner call, this guide will show you how to use ibm system partner mode in Ram-Guards under spring cloud framework.

## How to call Ram-Guards application by system partner

### Step1, register system partner application in ibm id.

Register [here](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html), select 'Client Credentials' grant type while registration.

Send the 'Client ID' in your ibm id registration and your valid ibm function id(should be a IIP email) to RAM-GUARDS application, so RAM-GUARDS application can config and authenticate your application correctly.

### Step2, get the client_credentials token from ibm id

Post a request to ibm id token endpoint with 4 params:

```shell script
curl --location --request POST 'https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic ${your_client_id:your_client_secret in base64}' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=openid'
```

Response should be like:

```json
{
  "access_token":"${access_token}",
  "scope":"openid",
  "grant_id":"${grant_id}",
  "id_token":"${id_token}",
  "token_type":"Bearer",
  "expires_in":7200
}
```

Get the access_token from the response as ibm id client_credentials token, notice that this access_token will be expired in 3599 seconds.


### Step3, get the Ram-Guards token from Ram-Guards authorization server

Post a request to Ram-Guards authorization server token endpoint with one param and two headers:

```http
POST /{zuul_route}/oauth/token HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_client_credentials_token_here}

Params: grant_type=password
```

Response should be like:

```json
{
    "access_token": "eyJ6aXAiOiJERUYiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..ca_Z86UPndfCH_6kJtskuA.B3s0uqIuir-Lk4dVyopgUQMMOqLoVAQvqDm1NMQOTZt7rVT-JnDAAX8xJQFzGFINpQGilfiJixvI-heFghX4n8t2sXFSQBLKZYBIZ3hocNSX-Bd3ItrMnbfrN4qs0sa8zQLFe5JujGlm5FA1u4hpBvQNrlTreqbaimR8KOhhjEilS9db_bEPuUWzWnDmoZM_bNYaTd2jFSJfl30GLwsx370StjCh7a5VqxCYNMxJUY0cs3dmaqbLDJGoNAxCOFYAX93CjEIi1yny-N3gHK3w4RhocMS8XwRw_iVFsqXYNgw.fEtC2001zW8FS_JB7RWVNQ",
    "token_type": "bearer",
    "refresh_token": "eyJ6aXAiOiJERUYiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..3XuJ8cwobOvYVHt-EmN6qw.rCPhNFcWnfNrhSb2G-9CTKZK3NNwQKwZ618KCLVt4DsjR-NDOLmrOsqqgDzMcL1YRpCDeuSRnp8xRAw1XJVjiQtU6PdEIvb9EZNnG2FfKozj_u9aGHGhrXx16BX98xxsYlL_5ilPb0q51s8YO2ea8l0xS3Gtyl9WZphthT58-2-7GcYcnFnNRy3uvVeafOMJknAH1doyNoslkjiK0TD37tCS2dD02WN_bkn8lv7WgSY4xkLTlG8aJpPTI2BW446U.gXn_aVcVDAkvvJobPQHMhQ",
    "expires_in": 3599,
    "scope": "authority detail role"
}
```

Get the access_token from the response as your Ram-Guards token, notice that this access_token will be expired in 3599 seconds.

### Step4, request resources endpoints with ibm id client_credentials token and Ram-Guards token

Request resources endpoints with three headers:

```http
POST /{zuul_route}/{resource_endpoint} HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_client_credentials_token_here}
        Ram-Guards: {ram_guards_token_here}
```

Then you should get the response under your function id's authorization.

## How to config ibm system partner mode in Ram-Guards

### Step1, config system partner's authorization in ESW RAM with system partner's function id

Please follow the guide we gave before.

### Step2, config Ram-Guards authorization server with system partner's client id and function id

Please follow the guide [here](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-authorization-server-spring-boot-starter).

[![Jietu20190307-161053.jpg](https://i.loli.net/2019/03/07/5c80d51919dde.jpg)](https://i.loli.net/2019/03/07/5c80d51919dde.jpg)


### Step3, config Ram-Guards resource server with system partner's client id

Please follow the guide [here](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-resource-server-spring-boot-starter).

[![Jietu20190307-162502.jpg](https://i.loli.net/2019/03/07/5c80d56d46494.jpg)](https://i.loli.net/2019/03/07/5c80d56d46494.jpg)

### Step4, config Ram-Guards ibmid resource server with system partner's client id

Please follow the guide [here](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ibmid-resource-server-spring-boot-starter).

[![Jietu20190307-162605.jpg](https://i.loli.net/2019/03/07/5c80d5ac90b7f.jpg)](https://i.loli.net/2019/03/07/5c80d5ac90b7f.jpg)

This should be configured in spring cloud zuul service.

### Step5, config Ram-Guards esw-provider with system partner's client id

Please follow the guide [here](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-zuul-esw-provider-spring-cloud-starter)

[![Jietu20190307-162703.jpg](https://i.loli.net/2019/03/07/5c80d5dff0b6c.jpg)](https://i.loli.net/2019/03/07/5c80d5dff0b6c.jpg)

This should be configured in spring cloud zuul service.