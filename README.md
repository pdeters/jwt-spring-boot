# JWT Spring Boot

Example of authentication and authorization using JSON Web Tokens ([JWT](https://jwt.io)) in [Spring Boot](http://spring.io/projects/spring-boot), and integration tests with [Spock](http://spockframework.org/).

Authorization Service
- Validates credentials and generates JWT tokens containing roles.
- See: [AuthController](src/main/groovy/com/pdeters/web/AuthController.groovy), [AuthControllerIntegrationSpec](src/integTest/groovy/com/pdeters/web/AuthControllerIntegrationSpec.groovy)

Resource Service
- Authenticates based on valid JWT token in request, authorizes based on roles contained within the token.
- See: [BookController](src/main/groovy/com/pdeters/web/BookController.groovy), [BookControllerIntegrationSpec](src/integTest/groovy/com/pdeters/web/BookControllerIntegrationSpec.groovy)
