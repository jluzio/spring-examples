# create tmp data folder
mkdir /demo/data

# ingest
logstash --path.data /demo/data -f /demo/logstash-accesslog.conf
logstash --path.data /demo/data -f /demo/logstash-csv.conf
logstash --path.data /demo/data -f /demo/logstash-csv-mutate.conf
logstash --path.data /demo/data -f /demo/logstash-json.conf
logstash --path.data /demo/data -f /demo/logstash-json-mutate.conf
logstash --path.data /demo/data -f /demo/logstash-json-split.conf

# remove data
rm -rf /demo/data
