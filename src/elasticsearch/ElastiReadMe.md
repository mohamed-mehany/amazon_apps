# Elasticsearch Amazon Apps

## Installation:

### Install elastic search v5.1

- mac
```
    $ brew install elasticsearch 
```

- Ubuntu

```
    $ wget https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-1.7.2.deb
    $ sudo dpkg -i elasticsearch-1.7.2.deb
    
```

### Get the cluster name
```
	http://localhost:9200/_cluster/state?pretty
```
### Create elastic search index
```
   cd <PROJECT_PATH>/config
   
   ./elastic_search_script.sh
```




### Running the app:

- add the elastic search configuration to your config file  (you will find an example in `config/settings.example.json`) 
