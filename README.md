# Challenge Senior Software Engineer - Checkout API Challenge - Adrian Rebollo

## Pre-Requisites
* Java 11
* Docker
* Maven3

## Api Tech Stack
* Solution achieved with:
    * Maven 
    * Spring Boot
    * Swagger
    * Postgres Database -> 
        * i've chosen this due i'm experience working with it and have really good performance.
        * I considered that for a code-challenge and for DEV purposes it accomplishes with the needed features. 
    * Some Util libraries (Apache, Jackson, Hibernate Types, Testing)

## Restriction decisions
* Due to the exercise summary, i have not seen any DELETE operation described for both resources (Orders and Products), even it was a CRUD.
* So i tried to follow strictly what was requested.
* Products
    * Create a new product
    * Get a list of all products
    * Update a product
* Orders
    * Placing an order
    * Retrieving all orders within a given time period


## API environments
* Api have to work with an environment `.env` file.
* Usually i would not commit that file but it will help to build and bootstrap for non-devs (reviewers).


## API decisions
* I tried to follow principles described at level 3 of the Richardson Maturity Model (Restful + HATEOAS) trying to re-use API Resources.
* Maven multi-module API self-explained in each pom.
    * checkout-app
    * checkout-e2e
    * checkout-model
    * checkout-persistance
    * checkout-rest
    * checkout-service

* API is well described through comments and Swagger Api annotations/descriptions.
* API is started with empty DB
    * no loads or inserts into DB occur
    * But definitions are created with Flyway
    * I Added one Indexe which could be critical for searches
        * Index in Orders for placed_date field (used for search orders after `timePeriod`)
* Product
    * Get a product             -> GET to `/products/{id}` with as PathParam the Product ID to retrieve.
    * Get a list of products    -> GET to `/products/all/{offset}` with as PathParam the offset from which start the retrieval.
                                   This endpoint serves Pages of results, in chunks of 100 (configurable).
                                   ie: first call with 0 will return from 1 to 100, 
                                       second call with 1 will return from 101 to 200...
                                   This has been decided to not overload DDBB nor JVM memory loading thousands of objects in memory.
    * Create a new product      -> POST to `/products` with as RequestBody the Product to insert without ID, consuming "application/json".
                                   The response is an URI resource (Header: Location) pointing to the created Resource.
                                   ie: /products/d56a4180-65aa-42ec-a945-5fd21dec0538
    * Update a product          -> PUT to `/products/{id}` with as RequestBody the Product to modify without ID (name, price or currency), consuming "application/json".
                                   Decided to use PUT as its idempotent, sending all Product data, which can be partially updated, and also refers the concrete Resource to update (/products/{id}).
                                   The response is an URI resource (Header: Location) pointing to the created Resource.
                                   ie: /products/d56a4180-65aa-42ec-a945-5fd21dec0538

* Orders
    * Placing an order          -> POST to `/orders` with as RequestBody the Product data with which create the order (Product Ids List and the Order Currency), consuming "application/json".
    * Retrieving by timePeriod  -> GET to `/orders/{timePeriod}` with as PathParam the time period from which retrieve the orders.
                                   This endpoint serves Pages of results, in chunks of 100 (configurable).
                                   Will retrieve orders created After the given timePeriod.
                                   The format of the timePeriod must follows `yyyy-MM-ddTHH:mm:ss`.

* There some restrictions for parameters:
    * Offset could not be null nor lower than 0.
    * currencies must exist and have 3 digits (EUR, USD, CHF....)
    * timePeriod must be before `currentDate`
    * a new Product could not be null, and all its fields are required (name not null/empty, price > 0 and a valid currency).
    * a new Order must contain a list (not null nor empty) of existing Product IDs, and a valid Currency and not empty/blank and valid email.
        * Validations have been achieved through Javax.Validation:
            * see `com.adrian.rebollo.validator.*`
    
## Model decisions
* Entities are mapped as ORM with Hibernate
* There are two main tables in DDBB
    * `products`
    * `orders`
* Their IDs are UUID.
* There are no Reference Integrity keys between tables. Why ?
    * In the Exercise is requested to keep the orders price with the product prices at the moment of creating the order.
    * so it had not much sense to fully/lazy load/link related objects cause its information was only relevant at the moment of creating an order, not anymore.
        * consistency is kept through the code.
            * ie: could not add unexisting products to an order
    * This has been solved adding a field in `orders` with the static data with which it was created (field `product_list`) as jsonb
        * initially, this field was a simple Array of Product Ids (`string`), but this delivered the problem of not knowing where the order price was coming from, without knowing the product details.
            * cause the products can be updated/changed.
        * Orders have their own currency with which calculate the order price.
        * products also have a currency and price.
            * so if an order have 2 produts which currencies are different, the order price is calculated based on the order currency. 
                * ie: Order 1 - currency USD - with product1 and product2
                      Product1 - price 1 - currency EUR
                      Product2 - price 2 - currency USD
                      Order 1 price = 1EUR + 1USD = 1.92
                      * Currency exchanges are calculated with a library `org.javamoney.moneta`
                * in addition, all these product details (prices currencies) are saved in the `order` field `product_list` as jsonb even if the product is changed afterwards.
* I addded `@Version` attribute to the persisted entities to keep data consistency and preventing concurrent data modifications (`optimistic locking`).
* The models offered out of the API as response/request, are in the `dto` package at `com.adrian.rebollo.dto`.

## Service Layer
* If an `optimistic locking` exception occurs, i configured a `retry` operation too, over these methods, to re-run that process and accomplish these transactions.
    * configured to maxAttempts 5 with a backoff of 1s.


## Design decisions
* The API has a javax.validation module (`rest/com.adrian.rebollo.validator`) which takes care about basic validation (annotation oriented).
* There are two main controllers.
    * Products
    * Orders
* There are two global handlers to catch API exceptions.
    * One is responsible to get javax.validation exceptions and map them correctly to be offered out of the API.
        * Have highest precedence in the global-handler logic chain.
    * The other handler catches checked and unchecked exceptions to correctly map and log them.
* Services are singleton and stateless
    * Their logic responsibility is basically where the  main-core functions belong.
        * calculate prices.
        * keep consistency.
* Any business flow would go through:
    * `javax.validation -> Controller -> Service -> Port -> Repository`
* If the application would be restarted, reboot or bootstrapped again, theres a mapped `volume` which would kept the `ddbb` data (`./checkout-db`).
* Services have transaction integrity to keep consistency between the DB operations inside the same process (`@Transaction`).
* Persisted entities live only in the `persistence` module, so DTOs are the incoming and outcoming objects from/to that layer, but inside is where entities are built.  
* The main indexes have been created to optimize searches over the `orders` table.
    * search by orders which `placed_date` is after `timePeriod`.   
* I configured some log files for errors, warnings and general spring/STDOUT.
    * they will be stored in docker container
        * explained how to see them below.
    * see `checkout-api/checkout-app/src/main/resources/logback-spring.xml`
* Only 2 profiles are present (`dev` and `prod`).

## Other decisions taken
* Code is carefully commented.
* Followed KISS and SOLID principles.
* Avoided to use any Cache, due to the hight volatility of information and understanding that this would not be much beneficial.
    * maybe just to store products by ID.
* I've been thinking about not limit/paginate the results which are returned as a List.
    * i didn't want to break any exercise requirement.
    * i thought to go with this strategy as i think is the one which takes more care about API performance.

## Testing
* jacoco plugin added to configure coverages/acceptance criteria around CI builds.
* note about 
* All tests combined (Integration + Unit) provide a coverage of 93% of classes and 96% of lines and methods.
* Testing has been developed at same time than development.

![Coverage](./pic/coverage.png?raw=false "Coverage")


### Unit Testing
* Added Unit Tests for all business critical parts.
* More unit testing could be introduced but i thought it was enough for a minnimum acceptance criteria.

### Integration Testing
* They live in the `checkout-app` module and are name-appended with `IT`.
* They are run bootstrapping the application plus the DDBB in a test-container
    * used `org.testcontainer.junit-jupiter` library to achieve it.
* Developed with Spring test libraries.
* Added several Integration Tests for critical business/technical points.
* Added Integration Tests also for errors and validations too.

### e2e Testing
* They live in the `checkout-e2e` module and are name-appended with `E2E`.
* They are run bootstrapping the application plus the DDBB in a test-container
    * used `org.testcontainer.junit-jupiter` library to achieve it.
* Developed with Spring test libraries.

### Load Tests
* Apache Benchmark Load test has been done to stress requests at the API.
    * Another Solution like JMeter could be chosen to detect Hot-spots or time-responses issues for desired methods, but i considered it a bit too far and out-of-scope.

## Compile, Package, Run, Test Instructions

### Compile with Maven
* `mvn clean compile -P[<empty>|<dev>`

### Package Artifact with Maven
* `mvn clean package -P[<empty>|<dev>`

### Test with Maven
* `mvn test -P[<empty>|<dev> {-Dskip.unit.tests=false}`

### Integration Tests with Maven
* `mvn test -P[<empty>|<dev> -DskipITTests=false {-Dskip.unit.tests=false}`

### Install Artifact with Maven
* `mvn clean install -P[<empty>|<dev>`

### Run API with Java
* `java -jar ./checkout-app/target/checkout-api-1.0.0-SNAPSHOT-exec.jar --spring.profiles.active=[<empty>|<dev>`

* Or with external yml config file
    * `java -jar ./checkout-app/target/checkout-api-1.0.0-SNAPSHOT-exec.jar --spring.profiles.active=[<empty>|<dev>|<test>|<prod>] --spring.config.location=<file_path>`

### Docker container bootstrapping
* build project and run container

* `docker-compose build`
* `docker-compose up`

* If preferred, project can be built with maven and use a dockerfile for already built projects.
* `mvn clean package -Pdev`
* `docker-compose -f ./checkout-api/docker-compose-built.yml build`
* `docker-compose -f ./checkout-api/docker-compose-built.yml up`

* Obtain Logs.
* `docker container ls`
* `docker cp <container_id>:/var/log /anypath`
    * by default logs are placed based on config property `logback.logs.folder: /var/log/checkout-api`

## Usage
* deploy application and go to:
    * `http://localhost:8080`
    * Will be redirected to:
    * `http://localhost:8080/swagger-ui.html`
        * All endpoints and it's parameters are described
            * see `checkout-api/checkout-rest/src/main/java/com/adrian/rebollo/controller`

## Integrations
* Added Travis CI integration config file at:
    * `./.travis.yml`

* Url of CI:
    * `https://travis-ci.com/adrys-lab/checkout-api/builds`
    
![Travis](./pic/travis.png?raw=false "Travis")