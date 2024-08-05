# References

## OpenSearch
### Curl
~~~bash
osPass='<pass>'
curl -X GET "https://localhost:9200" -ku admin:$osPass
curl -X GET "https://localhost:9200/_cat/nodes?v" -ku admin:$osPass
curl -X GET "https://localhost:9200/_cat/plugins?v" -ku admin:$osPass
~~~

### Dashboards
http://localhost:5601

### Dev Tools
http://localhost:5601/app/dev_tools

- index ops
~~~
GET wikimedia_change

DELETE wikimedia_change

GET wikimedia_change/_mapping

PUT wikimedia_change
<mapping>
~~~

- document ops
~~~
GET wikimedia_change/_doc/<id>

PUT wikimedia_change/_doc/<id>
<data>
~~~

- search
~~~
GET wikimedia_change/_search
{
  "query": { 
    "bool": { 
      "must": {
        "match": {
          "data.type": "log"
        }
      }
    }
  }
}
~~~
