# dropwizard-search
A demo project using dropwizard to search reviews

###compile###: 
 `mvn clean install`

###Start Application ###

`java -jar -Xmx1g target/review-search-1.0.0-rc5-SNAPSHOT.jar server config/configs.json`

###Rest Endpoints ###

* Search the review

`http://localhost:8000/api/v1/search?token=a,b,c`  (comma separated list)

* Generate the query-set
`http://localhost:8000/api/v1/querySet` - generate a file querySet.txt with 100K random queries


