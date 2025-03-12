# tracking-api
- API that generates unique tracking numbers for parcels.


### Run Server
- mvn clean package
- java -jar target/tracking-api-0.0.1-SNAPSHOT.jar 

### API CURL
    curl --location 'http://localhost:8080/api/next-tracking-number?origin_country_id=MY&destination_country_id=ID&customer_id=1234&weight=1.2345&created_at=2025-11-20T19%3A29%3A32+08%3A01&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics'

### Response 
    {
    "tracking_number": "1ZTLT6454K6FYD8Y",
    "created_at": "2025-03-12T14:28:55.846695Z"
    }