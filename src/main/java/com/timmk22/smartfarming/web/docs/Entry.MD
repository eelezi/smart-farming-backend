Planting Information API Documentation
Base URL: /entries

All endpoints under this controller handle CRUD operations for planting information entries. Authentication is implemented using JWT (JSON Web Token) via a Bearer token passed in the Authorization header.

Error Response Structure
All business logic errors thrown as IllegalArgumentException are handled by the global exception handler and return the following structure:

Status: 400 Bad Request or 404 Not Found depending on the context.

json
{
"status": 400,
"error": "Bad Request",
"message": "<error message>",
"path": "<request path>"
}
Unauthorized requests return 401 Unauthorized:

json
{
"status": 401,
"error": "Unauthorized",
"message": "<error message>",
"path": "<request path>"
}
Endpoints Overview
Method	Endpoint	Description	Auth Required
GET	/entries	Get all planting entries for the authenticated user	Yes
GET	/entries/{id}	Get a specific planting entry by ID	Yes
POST	/entries	Create a new planting entry	Yes
PUT	/entries/{id}	Update an existing planting entry	Yes
DELETE	/entries/{id}	Delete a planting entry	Yes


GET /entries
Retrieves all planting entries for the authenticated user.

Request
Headers:

Code
Authorization: Bearer <token>
Response
Status: 200 OK

json
[
{
"plantingId": 1,
"area": 2.5,
"plantingDate": "2026-04-01",
"userId": 1,
"cropId": 10,
"cropName": "Corn",
"soilTypeId": 3,
"soilTypeName": "Loamy",
"latitude": 41.9383,
"longitude": 21.5021,
"locationName": "Dracevo",
"irrigationType": "DRIP",
"currentStatus": "HEALTHY",
"expectedHarvestDate": "2026-09-15",
"notes": "Planted early spring"
}
]
Error Responses

404 Not Found — User not found

GET /entries/{id}
Retrieves a specific planting entry by ID.

Request
Headers:

Code
Authorization: Bearer <token>
Path Parameter:

id (Long) — Planting entry ID

Response
Status: 200 OK

json
{
"plantingId": 1,
"area": 2.5,
"plantingDate": "2026-04-01",
"userId": 1,
"cropId": 10,
"cropName": "Corn",
"soilTypeId": 3,
"soilTypeName": "Loamy",
"latitude": 41.9383,
"longitude": 21.5021,
"locationName": "Dracevo",
"irrigationType": "DRIP",
"currentStatus": "HEALTHY",
"expectedHarvestDate": "2026-09-15",
"notes": "Planted early spring"
}
Error Responses

404 Not Found — Entry not found or user does not have permission

POST /entries
Creates a new planting entry.

Request
Headers:

Code
Content-Type: application/json
Authorization: Bearer <token>
Body:

json
{
"area": 2.5,
"plantingDate": "2026-04-01",
"cropId": 10,
"soilTypeId": 3,
"latitude": 41.9383,
"longitude": 21.5021,
"locationName": "Dracevo",
"irrigationType": "DRIP",
"currentStatus": "HEALTHY",
"expectedHarvestDate": "2026-09-15",
"notes": "Planted early spring"
}
Field Validation:

Field	Type	Required	Constraints
area	Double	Yes	Positive
plantingDate	LocalDate	Yes	Valid date
cropId	Long	Yes	Positive
soilTypeId	Long	Yes	Positive
latitude	Double	No	Between -90 and 90
longitude	Double	No	Between -180 and 180
locationName	String	No	Max 255 characters
irrigationType	Enum	No	Valid irrigation type
currentStatus	Enum	No	Defaults to HEALTHY
expectedHarvestDate	LocalDate	No	Valid date
notes	String	No	Max 1000 characters


Response
Status: 201 Created

json
{
"plantingId": 1,
"area": 2.5,
"plantingDate": "2026-04-01",
"userId": 1,
"cropId": 10,
"cropName": "Corn",
"soilTypeId": 3,
"soilTypeName": "Loamy",
"latitude": 41.9383,
"longitude": 21.5021,
"locationName": "Dracevo",
"irrigationType": "DRIP",
"currentStatus": "HEALTHY",
"expectedHarvestDate": "2026-09-15",
"notes": "Planted early spring"
}
Error Responses

400 Bad Request — Validation error or crop/soil type not found

PUT /entries/{id}
Updates an existing planting entry.

Request
Headers:

Code
Content-Type: application/json
Authorization: Bearer <token>
Path Parameter:

id (Long) — Planting entry ID

Body:

json
{
"area": 3.0,
"plantingDate": "2026-04-05",
"cropId": 11,
"soilTypeId": 4,
"latitude": 41.9400,
"longitude": 21.5000,
"locationName": "Bitola",
"irrigationType": "SPRINKLER",
"currentStatus": "NEEDS_ATTENTION",
"expectedHarvestDate": "2026-09-20",
"notes": "Changed irrigation method"
}
Response
Status: 200 OK

json
{
"plantingId": 1,
"area": 3.0,
"plantingDate": "2026-04-05",
"userId": 1,
"cropId": 11,
"cropName": "Wheat",
"soilTypeId": 4,
"soilTypeName": "Clay",
"latitude": 41.9400,
"longitude": 21.5000,
"locationName": "Bitola",
"irrigationType": "SPRINKLER",
"currentStatus": "NEEDS_ATTENTION",
"expectedHarvestDate": "2026-09-20",
"notes": "Changed irrigation method"
}
Error Responses

404 Not Found — Entry not found or user does not have permission

400 Bad Request — Validation error or crop/soil type not found

DELETE /entries/{id}
Deletes a planting entry.

Request
Headers:

Code
Authorization: Bearer <token>
Path Parameter:

id (Long) — Planting entry ID

Response
Status: 204 No Content

Error Responses

404 Not Found — Entry not found or user does not have permission