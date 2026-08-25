# spring-boot-mongodb-codelab

Capstone project: persistence layer with **Spring Data MongoDB** on a **MongoDB Atlas** cluster.

REST API for **Product** (inventory) management with full CRUD operations.

## Stack

- Java 17
- Spring Boot 3.3.4 (Web, Data MongoDB, Validation)
- MongoDB Atlas (Cluster0)
- Maven

## Part 1 — Setup and connection to MongoDB Atlas

### 1. Create the cluster on MongoDB Atlas

1. Create an account / project on [MongoDB Atlas](https://cloud.mongodb.com).
2. Create a free cluster (`Cluster0`).
3. Under **Database Access**, create a database user (e.g. `ander`) with a strong password and read/write permissions.
4. Under **Network Access**, add the entry `0.0.0.0/0` (allow access from anywhere) — recommended for development/codelab environments only.
5. Under **Connect > Drivers**, copy the SRV connection string:

   ```
   mongodb+srv://ander:<db_password>@cluster0.yts4paf.mongodb.net/?appName=Cluster0
   ```

6. Replace `<db_password>` with the actual user password and, if the password contains special characters (`@`, `:`, `/`, `%`, etc.), URL-encode them (see [urlencoder.org](https://www.urlencoder.org/)).
7. Add the database name to use in the URI (e.g. `codelab_db`) before the query parameters:

   ```
   mongodb+srv://ander:<encoded_password>@cluster0.yts4paf.mongodb.net/codelab_db?appName=Cluster0
   ```

### 2. Dependency in `pom.xml`

Already added in this project:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### 3. `MONGODB_URI` environment variable

**The password is never written in the source code or in `application.properties`.** The full URI is injected via an environment variable:

```properties
spring.data.mongodb.uri=${MONGODB_URI}
```

How to set it locally:

**PowerShell (Windows), current session:**
```powershell
$env:MONGODB_URI = "mongodb+srv://ander:<encoded_password>@cluster0.yts4paf.mongodb.net/codelab_db?appName=Cluster0"
```

**PowerShell (Windows), persistent for the user:**
```powershell
setx MONGODB_URI "mongodb+srv://ander:<encoded_password>@cluster0.yts4paf.mongodb.net/codelab_db?appName=Cluster0"
```
(close and reopen the terminal/IDE for it to take effect)

**bash/zsh (Linux/Mac or Git Bash):**
```bash
export MONGODB_URI="mongodb+srv://ander:<encoded_password>@cluster0.yts4paf.mongodb.net/codelab_db?appName=Cluster0"
```

In **IntelliJ IDEA**: `Run > Edit Configurations > Environment variables` and add `MONGODB_URI=...`.

### 4. Run and verify the connection

```bash
mvn spring-boot:run
```

If the connection succeeds, the startup logs will show the Mongo driver describing the cluster (`org.mongodb.driver.cluster`) with no authentication or DNS resolution errors. You can also confirm it by creating a product (see Part 2) and checking that it appears in **Atlas > Collections**.

## Part 2 — Documents, repositories and CRUD

Code structure (`src/main/java/com/codelab/mongodb`):

```
model/        Product.java              -> @Document mapped to the "products" collection
repository/   ProductRepository.java    -> extends MongoRepository<Product, String>
service/      ProductService.java       -> interface with the business operations
service/impl/ ProductServiceImpl.java   -> @Service, implements the CRUD using the repository
controller/   ProductController.java    -> @RestController, exposes the REST API
exception/    GlobalExceptionHandler.java, ResourceNotFoundException.java
```

### Endpoints

Base URL: `http://localhost:8080/api/products`

| Method | Endpoint                   | Description                                        |
|--------|-----------------------------|-----------------------------------------------------|
| POST   | `/api/products`             | Create a product                                    |
| GET    | `/api/products`             | List all products                                   |
| GET    | `/api/products?name=xyz`    | Search products by name (case-insensitive, contains)|
| GET    | `/api/products/{id}`        | Get a product by id                                 |
| PUT    | `/api/products/{id}`        | Update an existing product                          |
| DELETE | `/api/products/{id}`        | Delete a product                                    |

### Sample body (POST / PUT)

```json
{
  "name": "Mechanical keyboard",
  "description": "Red switches, backlit",
  "price": 49.99,
  "stockQuantity": 25
}
```

### Testing with Postman

1. Run the application (`mvn spring-boot:run`) with `MONGODB_URI` configured.
2. **POST** `http://localhost:8080/api/products` with the sample body above -> should respond `201 Created` with the product and its generated `id`.
3. **GET** `http://localhost:8080/api/products` -> should list the created product.
4. **GET** `http://localhost:8080/api/products/{id}` with the returned id -> should return that product (`404` if it doesn't exist).
5. **PUT** `http://localhost:8080/api/products/{id}` with modified data -> should respond `200 OK` with the applied changes.
6. **DELETE** `http://localhost:8080/api/products/{id}` -> should respond `204 No Content`.
7. Verify in **MongoDB Atlas > Browse Collections > codelab_db > products** that the documents reflect the operations above.

## Running the tests

```bash
mvn test
```

The Spring context is started with a fake local URI (`src/test/resources/application.properties`) so this step doesn't depend on the real cluster or on the environment variable.
