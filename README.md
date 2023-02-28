# WA2-Group17-Lab5

## Vault

```
1. Pull & run Vault Docker container

# docker pull vault
# docker run --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=hvs8AqIBVD02kiTT3mVSbryJeer' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200' -p 8200:8200 --name vault-dev vault


2. Config vault

# Open Docker Desktop -> Containers -> Click on 'vault-dev' container -> open CLI

# export VAULT_ADDR='http://0.0.0.0:8200'
# export VAULT_TOKEN='hvs8AqIBVD02kiTT3mVSbryJeer'
# vault login hvs8AqIBVD02kiTT3mVSbryJeer
# mkdir /vault-secrets/
# echo '{"authentication_jwt_secret": "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4","authentication_turnstile_jwt_secret": "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4","eureka_password": "group17","eureka_username": "webapps2","ticket_validation_jwt_secret": "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"}' >> /vault-secrets/application.json
# echo '{"datasource_password":"wa2g17","datasource_url":"jdbc:postgresql://localhost:5432/postgres","datasource_username":"postgres","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/login_service.json
# echo '{"datasource_password":"wa2g17","datasource_url":"r2dbc:postgresql://localhost:5432/payment_service_db","datasource_username":"postgres","kafka_bootstrap_servers":"localhost:29092","kafka_consumer_group_id":"it.polito.wa2.paymentservice","kafka_template_default_topic":"ticketCatalogueService_paymentService","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/payment_service.json
# echo '{"datasource_password":"wa2g17","datasource_url":"r2dbc:postgresql://localhost:5432/ticket_catalgoue_service_db","datasource_username":"postgres","kafka_bootstrap_servers":"localhost:29092","kafka_consumer_group_id":"it.polito.wa2.ticketcatalogueservice","kafka_template_default_topic":"it.polito.wa2.ticketcatalogueservice","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/ticket_catalogue_service.json
# echo '{"datasource_password":"wa2g17","datasource_url":"jdbc:postgresql://localhost:5432/postgres","datasource_username":"postgres","kafka_bootstrap_servers":"localhost:29092","kafka_consumer_group_id":"it.polito.wa2.ticketvalidationservice","kafka_template_default_topic":"ticketValidationService_travelerService","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/ticket_validation_service.json
# echo '{"datasource_password":"wa2g17","datasource_url":"jdbc:postgresql://localhost:5432/traveler_service_db","datasource_username":"postgres","kafka_bootstrap_servers":"localhost:29092","kafka_consumer_group_id":"it.polito.wa2.travelerservice","kafka_template_default_topic":"it.polito.wa2.travelerservice","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/traveler_service.json
# echo '{"datasource_password":"wa2g17","datasource_url":"jdbc:postgresql://localhost:5432/turnstile_service_db","datasource_username":"postgres","mail_host":"smtp.gmail.com","mail_password":"qdemdybvtmvhgsem","mail_port":"587","mail_smtp_auth":"true","mail_smtp_starttls_enable":"true","mail_smtp_starttls_required":"true","mail_username":"noreply.wa2g17@gmail.com"}' >> /vault-secrets/turnstile_service.json
# vault kv put secret/application @/vault-secrets/application.json
# vault kv put secret/Login-Service @/vault-secrets/login_service.json
# vault kv put secret/Payment-Service @/vault-secrets/payment_service.json
# vault kv put secret/Ticket-Catalogue-Service @/vault-secrets/ticket_catalogue_service.json
# vault kv put secret/Ticket-Validation-Service @/vault-secrets/ticket_validation_service.json
# vault kv put secret/Traveler-Service @/vault-secrets/traveler_service.json
# vault kv put secret/Turnstile-Service @/vault-secrets/turnstile_service.json
```

## Kafka & Zookeeper docker-compose.yml file

```
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
 ```

# TICKET CATALOGUE SERVICE

## DDL statements
-Creation of the database `ticket_catalgoue_service_db` and definition of `ticket_order` and `ticket_details` tables.
``` sql
create table ticket_orders
(
    user_nickname           varchar(255)                   not null,
    num_tickets             integer                        not null,
    ticket_type             varchar(255)                   not null,
    ticket_type_id          bigint                         not null,
    order_price             double precision               not null,
    credit_card_number      varchar(255)                   not null,
    card_holder             varchar(255)                   not null,
    order_placed_timestamp  varchar(255)                   not null,
    tickets_generated_status          varchar(255)                   not null,
    tickets_generated_timestamp       varchar(255),
    payment_status          varchar(255)                   not null,
    payment_timestamp       varchar(255),
    order_id                uuid default gen_random_uuid() not null
        constraint ticket_orders_pk
            primary key
);

alter table ticket_orders
    owner to postgres;




create table ticket_details
(
    ticket_id serial                        not null,
    type      varchar(255)                  not null,
    price     double precision              not null,
    min_age   integer default '-1'::integer not null,
    max_age   integer default '-1'::integer not null,
    start_day varchar(255)                  not null,
    end_day   varchar(255)                  not null,
    zid       varchar(255)                  not null
);

alter table ticket_details
    owner to postgres;


```

# PAYMENT SERVICE

## DDL statements
-Creation of the database `payment_service_db` and definition of `transactions` table.

``` sql
create database payment_service_db
with owner postgres;

create table transactions
(
    order_id           varchar(255)                 not null
        constraint transactions_pk
            primary key,
    user_nickname      varchar(255)                 not null,
    order_price        double precision default 0.0 not null,
    credit_card_number varchar(255)                 not null,
    card_holder        varchar(255)                 not null,
    payment_timestamp  varchar(255)                 not null,
    payment_successful boolean                      not null,
    payment_refunded   boolean                      not null
);

alter table transactions
    owner to postgres;
```

## HTTP Testing script

### http-client.env.json

```
{
  "dev": {
    "userToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpb2RlZGEiLCJyb2xlcyI6IlJPTEVfQ1VTVE9NRVIiLCJpYXQiOjE2NjM0MzI2MzQsImV4cCI6MjY2MzQzNjIzNH0.CMckvIRBxPgv8bnNw-zGLxNiQgs3Xzu21OC8Czr8Bgw",
    "adminToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpb2RlZGEiLCJyb2xlcyI6IlJPTEVfQURNSU4iLCJpYXQiOjE2NjM0MzI2MzQsImV4cCI6MjY2MzQzNjIzNH0.V5LDzOWPUs4j_dbwEC3nyyc0aUOC-64ilezcwHf9ysg",
    "turnstileToken": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiem9uZSI6IkFCQ0RFRkdIIiwiZXhwIjo0ODE5MTQ1ODE2fQ.hbEKuqTpgRrLQmddUIfgK3Dllh0uLdrzVKSTk3ANZ4E"
  }
}
```


### generated-requests.http

```
###
POST http://localhost:8080/auth/admin/createAccount
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
"nickname": "",
"email": "String",
    "password":"String",
    "role":"String",
    "adminManageCustomerAccounts":"false",
    "adminManageAdminAccounts":"false",
    "adminManageTickets":"false",
    "adminManageTravelers":"false",
    "adminManageOrders":"false"
    }

###
POST http://localhost:8080/auth/public/register
Content-Type: application/json

{"nickname":"mariodeda97","email":"mario.deda.97@gmail.com","password":"Abc123?."}

###
POST http://localhost:8080/auth/public/validate
Content-Type: application/json

{"provisionalId": "51fad051-4ee7-4d06-9549-f23fb947c46d", "activationCode": "d97ba76e-f0ec-4a99-bfed-0f43129c179d"}

<> 2022-09-17T182331.500.json

###
POST http://localhost:8080/auth/public/login
Content-Type: application/json

{"nickname":"mariodeda", "password":"Abc123?."}







###
GET http://localhost:8080/payment/admin/transactions
Authorization: Bearer {{adminToken}}

<> 2022-09-18T005702.200.json
<> 2022-09-18T004952.503.json
<> 2022-09-18T004947.503.json
<> 2022-09-18T004942.503.json
<> 2022-09-18T004933.503.json
<> 2022-09-18T004622.503.json
<> 2022-09-18T004615.503.json
<> 2022-09-18T004133.500.json

###
GET http://localhost:8080/payment/user/transactions
Authorization: Bearer {{userToken}}

<> 2022-09-18T005718.200.json
<> 2022-09-18T004957.500.json
<> 2022-09-18T004949.503.json
<> 2022-09-18T004944.503.json
<> 2022-09-18T004939.503.json
<> 2022-09-18T004937.503.json
<> 2022-09-18T004625.500.json
<> 2022-09-18T004620.503.json
<> 2022-09-18T004134.500.json







###
GET http://localhost:8080/ticket/catalogue/admin/orders
Authorization: Bearer {{adminToken}}

<> 2022-09-18T005742.200.json

###
GET http://localhost:8080/ticket/catalogue/admin/orders/mariodeda
Authorization: Bearer {{adminToken}}

<> 2022-09-18T005746.200.json

###
GET http://localhost:8080/ticket/catalogue/admin/tickets
Authorization: Bearer {{adminToken}}

<> 2022-09-18T005752.200.json
<> 2022-09-18T003851.405.json

###
POST http://localhost:8080/ticket/catalogue/admin/tickets
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "type":"STUDENT",
  "price":"5.30",
  "minAge":"18",
  "maxAge":"20",
  "startDay":"2022-09-17 00:00",
  "endDay":"2023-09-17 00:00",
  "zid":"ABCDEF"
}


###
GET http://localhost:8080/ticket/catalogue/user/orders
Authorization: Bearer {{userToken}}

###
GET http://localhost:8080/ticket/catalogue/user/orders/6e9ca226-551d-44f0-b8d3-1b47f9d92d91
Authorization: Bearer {{userToken}}
Content-Type: application/json

<> 2022-09-18T005955.200.json
<> 2022-09-18T005931.200.json

###
POST http://localhost:8080/ticket/catalogue/user/shop/1
Authorization: Bearer {{userToken}}
Content-Type: application/json

{
  "n_tickets":"15",
  "ticket_id":"1",
  "creditCardNumber":"1234123412341234",
  "expirationDate":"04/24",
  "cvv":"123",
  "cardholder":"Mario Deda"
}

<> 2022-09-18T005938.200.json
<> 2022-09-18T005858.200.json

###
GET http://localhost:8080/ticket/catalogue/user/tickets
Authorization: Bearer {{userToken}}
Content-Type: application/json






###
POST http://localhost:8080/ticket/validation/embedded/validate

###
POST http://localhost:8080/ticket/validation/user/validate










###
POST http://localhost:8080/traveler/admin/report
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "startTime": "2022-01-01 00:00",
  "endTime": "2022-12-31 23:59"
}

<> 2022-09-18T002707.200.json
<> 2022-09-18T000855.200.json
<> 2022-09-18T000610.500.json

###
POST http://localhost:8080/traveler/admin/report/user
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "userID": "mariodeda",
  "startTime": "2022-01-01 00:00",
  "endTime": "2022-12-31 23:59"
}

<> 2022-09-18T010011.200.json
<> 2022-09-18T002450.200.json


###
GET http://localhost:8080/traveler/admin/report/5764322c-aec0-4211-9db1-21bf19df64fa
Authorization: Bearer {{adminToken}}

<> 2022-09-18T010022.200.json
<> 2022-09-18T003533.200.json
<> 2022-09-18T002716.200.json
<> 2022-09-18T000918.200.json

###
GET http://localhost:8080/traveler/admin/traveler/mariodeda/profile
Authorization: Bearer {{adminToken}}

###
GET http://localhost:8080/traveler/admin/traveler/mariodeda/tickets
Authorization: Bearer {{adminToken}}

<> 2022-09-18T000417.200.json

###
GET http://localhost:8080/traveler/admin/travelers
Authorization: Bearer {{adminToken}}

<> 2022-09-18T000232.200.json

###
GET http://localhost:8080/traveler/user/my/profile
Authorization: Bearer {{userToken}}
Content-Type: application/json

###
PUT http://localhost:8080/traveler/user/my/profile
Authorization: Bearer {{userToken}}
Content-Type: application/json

{
  "name":"Mario Bobi",
  "address":"Torino",
  "dateOfBirth":"1997-11-30",
  "telephoneNumber":"1234567890"
}

###
GET http://localhost:8080/traveler/user/my/tickets
Authorization: Bearer {{userToken}}

<> 2022-09-18T000126.200.json

GET http://localhost:8080/traveler/user/my/tickets/qr/e2eba0fa-f3e3-4893-a942-22302aca0626
Authorization: Bearer {{userToken}}

###
GET http://localhost:8080/traveler/user/my/tickets/d03bf4c8-2753-4c77-8173-5156b0edb073
Authorization: Bearer {{userToken}}

<> 2022-09-18T000203.200.json











###
GET http://localhost:8080/turnstile/admin/turnstiles
Authorization: Bearer {{adminToken}}
Content-Type: application/json

<> 2022-09-18T002122.200.json

###
GET http://localhost:8080/turnstile/admin/turnstile/2
Authorization: Bearer {{adminToken}}
Content-Type: application/json

<> 2022-09-18T002142.406.txt
<> 2022-09-18T002136.200.json

###
POST http://localhost:8080/turnstile/admin/add
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "zid": "ABCDEFGH",
  "disabled": false
}

<> 2022-09-18T002016.200.txt
<> 2022-09-18T001902.503.json

###
DELETE http://localhost:8080/turnstile/admin/1

###
PUT http://localhost:8080/turnstile/admin/1/

###
POST http://localhost:8080/turnstile/embedded/validate
Authorization: Bearer {{turnstileToken}}
Content-Type: application/json

{"jws_ticket": "eyJhbGciOiJIUzI1NiJ9.eyJ2eiI6IkFCQ0RFRiIsInZhbGlkRnJvbSI6MTY2MzM2NTYwMDAwMCwidHlwZSI6IkZBTUlMWSIsInN1YiI6IjU2NzNlNTBiLWJlZWYtNDEwYS05MmNjLTk0NWJhOTEzMWFkYyIsImlhdCI6MTY2MzQ1MTk2MSwiZXhwIjoxNjk0OTAxNjAwfQ.7KflHrb9U_jDZeJp-oBP5Kc6JeE2z_kRYBb-mtlHbRA"}

```





