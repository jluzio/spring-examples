# ingest
filebeat --path.home /usr/share/filebeat --path.config /usr/share/filebeat --path.data /demo/tmp/data --path.logs /demo/tmp/logs -c /demo/filebeat.demo.yml
filebeat --path.home /usr/share/filebeat --path.config /usr/share/filebeat --path.data /demo/tmp/data --path.logs /demo/tmp/logs -c /demo/filebeat.setup.yml setup dashboards

# remove data
rm -rf /demo/tmp