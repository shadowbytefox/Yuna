# yuna

Yuna is a Profile managment API system in clojure


## Usage

| Command                                  | description   |
| ---                                      | ---           |  
| lein run help                            | Show help page|
| lein run config default                  | Show default config
| lein run config active                   | Show active config
| - lein run -c config.yaml -s secret.yaml | Use local or outside of k8s

Installation guide:

First we set up a Posgres database (there are enough instructions in the net)

now we have to install an extension
``` sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

Once this is done, we can create the "profile" table

For this we execute the following SQL query

``` sql
CREATE TABLE public.profile (
    id SERIAL NOT NULL,
    username text,
    identity uuid DEFAULT public.uuid_generate_v4(),
    language bigint,
    email text,
    password text,
    viewcode text,
    changecode text,
    verification_code text,
    verification boolean DEFAULT true,
    jwt text,
    role text
);

ALTER TABLE public.profile OWNER TO POSTGRES;

CREATE SEQUENCE public.profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.profile_id_seq OWNER TO postgres;
```
Depending on the user you need to change postgres to your username.

now we have set up Postgres and we need the following data:
```
(Demo Daten)
Username: postgres
Password: 123
Datenbank Namen: test_projekt
ip: 127.0.0.1
```

Now we can create the configuration file

config.yaml
``` yaml
Code:
services:
  Yuna Platform:
    webserver:
      port: 8080 
      aes-secret: ${AES_SECRET}
      jws-secret: ${JWS_SECRET}
      password-seed: ${PASSWORD_SEED}
      profile-db:
        dbtype: postgresql
        host: 127.0.0.1
        dbname: test_projekt
        user: postgres
        password: ${PG_PASSWORD}
  logging:
    log-level: debug
    log-encoder: pattern
  tracing:
    tracing-enabled: false
  metrics:
    metrics-ip: 0.0.0.0
    metrics-port: 9201
    metrics-jvm: false
```
We can already enter the obtained data except for the password under profile-db.

Now we only need the secret file, which looks like this

secret.yaml
``` yaml
Code:
AES_SECRET: CHANGE_ME
JWS_SECRET: CHANGE_ME
PG_PASSWORD: "123"
PASSWORD_SEED: CHANGE_ME
```

The other fields can be left as they are for demo purposes or you can generate safe seeds directly.

From here on we have everything to start the service

everyone who is not familiar with Clojure will ask how to start the service
Clojure runs on the Java JVM which makes the start very easy and simple

Code:
``` shell
#user~/ java -jar yuna-server.jar -c "PATH TO config.yaml" -s "PATH TO secret.yaml" start
```
if you want to view the active config, you can enter the following command

Code:
``` shell
#user~/ java -jar yuna-server.jar -c "PATH TO config.yaml" -s "PATH TO secret.yaml" config active
```

or if you want to see how the default config looks like

``` shell
#user~/ java -jar yuna-server.jar -c "PATH TO config.yaml" -s "PATH TO secret.yaml" config default
```

The service runs on port 8080 by default,
as soon as the service is running, it can be reached at 127.0.0.1:8080
You can change the port in config.yaml.

12-factor microservice framework: https://gitlab.com/dumonts/dwanascie


Let me know if you have problems or further questions



## License
Eclipse Public License - v 2.0 - shadowbytefox
