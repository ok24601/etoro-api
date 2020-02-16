### Etoro trading API


##### Build executable jar
````
./gradlew build
````
##### Set your account credentials into Environment variables: LOGIN, PASSWORD
````
export LOGIN=yourusername
export PASSWORD=yourpassword
````
##### Start API server
````
 java -jar build/libs/etoro-api-0.0.1-SNAPSHOT.jar
````

### Using API server

In order to trade you have to add asset to watchlist first, then you can open or close positions with this asset.
Your watchlist will be persisted locally in the watchlist.json if you start the server from the project folder.

- API documentation http://localhost:8088/etoro-api/v2/api-docs
- add assets to watchlist http://localhost:8088/etoro-api/watchlist/
- open/close positions http://localhost:8088/etoro-api/positions/



