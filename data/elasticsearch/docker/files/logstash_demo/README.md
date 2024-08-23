# ingest
logstash --path.data /demo/tmp -f /demo/logstash-accesslog.conf
logstash --path.data /demo/tmp -f /demo/logstash-csv.conf
logstash --path.data /demo/tmp -f /demo/logstash-csv-mutate.conf
logstash --path.data /demo/tmp -f /demo/logstash-json.conf
logstash --path.data /demo/tmp -f /demo/logstash-json-mutate.conf
logstash --path.data /demo/tmp -f /demo/logstash-json-split.conf
logstash --path.data /demo/tmp -f /demo/logstash-grok.conf
logstash --path.data /demo/tmp -f /demo/logstash-grok-multiple-patterns.conf
logstash --path.data /demo/tmp -f /demo/logstash-log-java-service.conf
logstash --path.data /demo/tmp --path.settings /demo/settings/dlq -f /demo/logstash-dlq.conf
logstash --path.data /demo/tmp --path.settings /demo/settings/dlq -f /demo/logstash-dlq-recover.conf

docker network connect kafka_default logstash
logstash --path.data /demo/tmp -f /demo/logstash-kafka.conf

# remove data
rm -rf /demo/tmp
