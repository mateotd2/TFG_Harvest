# HARVEST PROJECT

### 1- Generate apis and DTOs:

```  
    cd harvest-open-api
    mvn install
```

### 2- Start app:

```
    cd harvest-backend
    mvn install
    mvn spring-boot:run
```

#### Check SWAGGER-UI

http://localhost:8080/swagger-ui/index.html

Explore /v3/api-docs

#### Check OpenApi Definition

http://localhost:8080/v3/api-docs


### 3- Start frontend:

```
    flutter run -d chrome 
```