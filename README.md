### Etoro trading API
![CI Docker Image Build](https://github.com/mkjiau/etoro-api/workflows/CI%20Docker%20Image%20Build/badge.svg?branch=master)

#### Support project:
BTC: 3FRPsX4QsNNFzKZwtXh92JfKeetWJcn3SY

ETH: 0x8134888B3429aea645b34fcd576842a204d9C9aE

##### Requirments:
- java 11 (JDK11)
- Google Chrome (tested on version 78)
- internet connection

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
### Execute Requests

 All requests are in the postman collection under postman/
 import it to the Postman and try it out.

### Using API server

 All requests are in the postman collection under postman/
 import it to the Postman and try it out.

In order to trade you have to add asset to watchlist first, then you can open or close positions with this asset.
Your watchlist will be persisted locally in the watchlist.json if you start the server from the project folder.

- Swagger UI http://localhost:8088/etoro-api/swagger-ui.html
- API documentation http://localhost:8088/etoro-api/v2/api-docs
- add assets to watchlist http://localhost:8088/etoro-api/watchlist/
- open/close positions http://localhost:8088/etoro-api/positions/


### Examples

### Buy in Demo mode
### 1 Adding asset to your watchlist
#### Bitcoin
````
 curl -X PUT \
   http://localhost:8088/etoro-api/watchlist/byName \
   -H 'Content-Type: application/json' \
   -H 'cache-control: no-cache' \
   -d '{
 	"param": "btc"
 }'
````
##### NASDQ100
````
 curl -X PUT \
   http://localhost:8088/etoro-api/watchlist/byName \
   -H 'Content-Type: application/json' \
   -H 'cache-control: no-cache' \
   -d '{
 	"param": "nsdq100"
 }'
````
##### GOLD
````
 curl -X PUT \
   http://localhost:8088/etoro-api/watchlist/byName \
   -H 'Content-Type: application/json' \
   -H 'cache-control: no-cache' \
   -d '{
 	"param": "gold"
 }'
````
#### 1.1 Review your watchlist
````
curl -X GET \
  http://localhost:8088/etoro-api/watchlist \
  -H 'cache-control: no-cache' \
  -H 'mode: Demo'
````
### 2 Open position 
````
curl -X POST \
  http://localhost:8088/etoro-api/positions/open \
  -H 'Content-Type: application/json' \
  -H 'mode: Demo' \
  -d '{
	"name": "btc",
	"type": "BUY",
	"amount": 100,
	"leverage": 2,
	"takeProfitRate": 13000,
	"stopLossRate": 1000
}'
````
##### Make sure that "takeProfit" and "stopLoss" have valid values.
##### for some trades (all x1 buy) they are optional.
#### Response:
````
{
    "date": "2020-02-22T12:28:53.574543",
    "requestToken": "a859ec38-12bd-41b2-87f3-205515f2d608",
    "errorMessageCode": 0,
    "notificationParams": null,
    "position": {
        "leverage": 2,
        "stopLossRate": 8000,
        "takeProfitRate": 13000,
        "amount": 100,
        "instrumentID": "100000",
        "positionID": "1621284697",
        "isBuy": true,
        "isTslEnabled": false,
        "view_MaxPositionUnits": 0,
        "view_Units": 0,
        "view_openByUnits": null,
        "isDiscounted": false,
        "viewRateContext": null,
        "openDateTime": "2020-02-22T11:28:53.3993309",
	"openRate": 10100.1
    }
}
````
#### you can use positionID to close this position later on.

### Close BTC position in Demo mode
````
curl -X DELETE \
  'http://localhost:8088/etoro-api/positions/close?id=1621284697' \
  -H 'mode: Demo'
````

