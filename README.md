# HARVEST PROJECT

### 1- Iniciar el backend con docker:
```  
    docker-compose up -d
```

#### 2- Comprueba los endpoints SWAGGER-UI

http://localhost:8080/swagger-ui/index.html



##### OpenApi Definition

http://localhost:8080/v3/api-docs


### 3- Inicia frontend:

Para probar la aplicación, uso este comando, desactivando CORS para realizar pruebas

```
    flutter run -d chrome --web-browser-flag "--disable-web-security"
```