**Federal Holidays RESTful API**

This project implements a RESTful API for managing federal holidays for countries Canada and USA. 
It allows adding, updating, and listing holidays for these countries.

**Technologies:**

Java 17
Gradle
H2 Database

**Testing**

The project has extensive unit and integration tests.

**API Documentation**

Swagger API documentation is available via Swagger UI: Open http://localhost:8080/swagger-ui/index.html 

## APIs for Holidays

Retrieves all holidays:
GET URL: {{base_url}}/api/holiday

Retrieves a holiday by its ID:
GET URL: {{base_url}}/api/holiday/{id} 

Retrieves List holidays for a specific country:
GET URL: {{base_url}}/api/holiday/{countryCode} 

POST URL: {{base_url}}/api/holiday/addAll Body: json [ { "name": "New Year", "date": "2024-01-01", "countryCode": "US" }, { "name": "Christmas", "date": "2024-12-25", "countryCode": "US" } ] 

Adds a single holiday:
POST URL: {{base_url}}/api/holiday/add Body: json { "name": "Independence Day", "date": "2024-07-04", "countryCode": "US" } 

Updates an existing holiday:
PUT URL: {{base_url}}/api/holiday/{id} Body: json { "name": "Updated Holiday Name", "date": "2024-12-25", "countryCode": "US" } 

Deletes a holiday by its ID:
DELETE URL: {{base_url}}/api/holiday/{id} 

update via file :
POST URL: {{base_url}}/api/holiday/upload

## APIs for Countries

Retrieves all countries:
GET URL: {{base_url}}/api/countries 

Retrieves a country by its code:
GET URL: {{base_url}}/api/countries/code/{code} 

Retrieves a country by its ID:
GET URL: {{base_url}}/api/countries/id/{id} 

Adds a new country:
POST URL: {{base_url}}/api/countries Body: json { "name": "Canada", "code": "CA" } 

Updates an existing country:
PUT URL: {{base_url}}/api/countries/{id} Body: json { "name": "Updated Country Name", "code": "CA" } 

Deletes a country by its ID:
DELETE URL: {{base_url}}/api/countries/{id} 
