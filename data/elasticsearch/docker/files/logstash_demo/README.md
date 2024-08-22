# create tmp data folder
mkdir /demo/data

# ingest
logstash -f /demo/logstash-demo.conf --path.data /demo/data

# remove data
rm -rf /demo/data
