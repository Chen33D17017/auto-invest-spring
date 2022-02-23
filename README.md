# Auto Invest Program
## Overview
This is a demo service which can allow the user setting the regular crypto invest with Binance

## API Endpoint
### AppUser Controller
Allow registration for the service user, including 
  - POST /v1/registration
  - GET /v1/registration/confirm
  - PATCH /v1/user
  - GET /v1/user
### Binance Gateway Controller
Allow user doing some operation related to Binance crypto exchange, including

  - POST /v1/order
  - POST /v2/order
  - GET /v1/status
  - GET /v1/history
  - POST /v1/migration

This controller need to contain the username, password on basic auth for authentication
### RegularInvest Controller
Register regular investment for the user, you also can change the setting with api
  - GET /regular_invest
  - POST /regular_invest
  - PUT /regular_invest
  - DELETE /regular_invest

This controller need to contain the username, password on basic auth for authentication

## Other
This application is for experimental usage, if you like to use, please do care about your own security.


## How to run
### Build Docker
```bash
mvn clean install
docker build -t auto-invest-sprint .
```

### Run Application
```bash
docker container run --network auto-invest_service-network -p 8080:8080 auto-invest-sprint
```
## TODO 
- Unit Test support
- Ip Whitelist Management
- logging improvement
- Adding Unit Test