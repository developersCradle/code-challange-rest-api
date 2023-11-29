# Backend task

Back Endissä tulisi olla REST rajapinnat käyttäjien lisäämiseen, poistamiseen ja muokkaamiseen, kaikkien käyttäjien hakemiseen siivutettuna sekä vielä käyttäjien hakemiseen nimen perusteella.

- Autentikaatiota ei välttämättä tarvita.
- Tietokannaksi voit valita minkä itse koet parhaaksi.

Rajapinnan datasta:

https://jsonplaceholder.typicode.com/users

### Checklist

- ~~Spring API documentation.~~ ✔️
[Open-API](https://springdoc.org/#getting-started) was used, because Swagger current version didn't support the newest Spring Boot. To look documentation, navigate to `http://localhost:8080/swagger-ui/index.html`
    - ~~Look for example [Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)~~
- ~~Some database~~ ✔️
MySQL
    - ~~Some way to initialize random users.~~ ✔️
    `@PostConstruct`
        - ~~For example MySQL or Spring Boot initializing way~~
            - ~~Creating database startup mechanism~~ ✔️
            Hibernate takes care of creating database and table 
- ~~Make instructions `.md`~~ ✔️
- ~~CRUD operations~~ ✔️
    - ~~Add~~ ✔️ 
    Adding user is done making **POST** message to `/api/v1/users`.
    - ~~Remove~~ ✔️ 
    Removing user is done making **DELETE** message to `/api/v1/users/{userId}`
    - ~~Modify **PUT**(PUT/POST/PATCH)~~ ✔️ 
    Modifying user is done making **PUT** message to `/api/v1/users`
    - ~~Read~~ ✔️ 
    Return all users when making **GET** request to `/api/v1/users`
        - ~~Return all as paged~~ ✔️
        Returns wanted amount of users, when perform **GET** message `/api/v1/users?pageSize=10` where `pageSize` URL parameter is present.
            - ~~Simple paging~~
                - ~~[Possible paging](https://docs.spring.io/spring-data/rest/docs/2.0.0.M1/reference/html/paging-chapter.html)~~
        - ~~Return users based on name~~ ✔️
        Returns all users based on name, when perform **GET** message to`/api/v1/users?name=pekka` here `name` URL parameter is present.
- ~~Tests~~ ✔️ 
Service level tests. 
    - ~~Following error were introduced when using plain old Eclipse `error java.lang.NoSuchMethodError: 'java.util.Set`.~~ ✔️
    This was fixed using `Spring Tools 4 for Eclipse`
    - Unit tests were not introduced since business logic was fairly simple.
    - ~~Option to add docker to tests, so test db and production db data won't get mixed up.~~ ✔️
- Other bonuses would be to implement sort, offset and indexes into query logic. (BONUS)
- For the sake of time authentication was not introduced. If there would be such a time, usage of tokens would be an option to go.
- ~~Get user with specified userId with GET message. (BONUS)~~ ✔️
Do **GET** request for following address `/api/v1/users/{userId}`

# How to run

> Requirements for running locally
> 1. Java JDK 17 minimum. Tested with [OpenJDK21](https://jdk.java.net/21/)
> 2. MySQL server running. Tested with [MySQL 8.x.xx](https://dev.mysql.com/downloads/installer/). Server should be running in port `3306` and root password **root** and root user **root**

> Maven should not be needed since using Maven wrapper `mvnw` 
> Docker is needed to run tests

1. Pull repository
2. Go to the project folder and build the .jar file and execute. `./mvnw spring-boot:run`
3. Go to documentation and start using `http://localhost:8080/swagger-ui/index.html`

- This has been tested. For the future, if there is time, there should be a docker image for setting this process up.
