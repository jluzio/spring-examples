# Reference
- https://www.elastic.co/docs


## Conf
- kibana
~~~md
# configure the Kibana password in the ES container
export ELASTIC_PASSWORD=changeme
export KIBANA_PASSWORD=kibana_pass
curl -u elastic:$ELASTIC_PASSWORD \
  -X POST \
  http://localhost:9200/_security/user/kibana_system/_password \
  -d '{"password":"'"$KIBANA_PASSWORD"'"}' \
  -H 'Content-Type: application/json'
~~~

- volumes
~~~bash
docker run --rm -it --volume 'elasticsearch_esdata:/esdata:ro' busybox sh
docker run --rm -it --volume 'elasticsearch_filebeat-data:/filebeat-data:ro' busybox sh
~~~

- filebeat
~~~bash
# basic filebeat image
docker run --rm -it docker.elastic.co/beats/filebeat:8.15.0 bash

# check filebeat volumes 
docker run --rm --user=root --volume="/var/lib/docker/containers:/var/lib/docker/containers:ro" --volume="/var/run/docker.sock:/var/run/docker.sock:ro" busybox bash
# or using filebeat image
docker run --rm --user=root --volume="/var/lib/docker/containers:/var/lib/docker/containers:ro" --volume="/var/run/docker.sock:/var/run/docker.sock:ro" -it docker.elastic.co/beats/filebeat:8.15.0 bash
~~~
