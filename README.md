### Etoro trading API

###to start API server:

##### Build executable jar
````
./gradlew build
````
##### Set your account credentials into Environment variables: USERNAME, PASSWORD
````
export LOGIN=yourusername
export PASSWORD=yourpassword
````
##### Start API server
````
 java -jar build/libs/etoro-api-0.0.1-SNAPSHOT.jar
````

### Using API server

- API documentation http://localhost:8088/etoro-api/v2/api-docs
- add assets to watchlist http://localhost:8088/etoro-api/watchlist/
- open/close positions http://localhost:8088/etoro-api/positions/



