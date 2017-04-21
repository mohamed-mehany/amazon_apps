#!/bin/bash
curl -XDELETE 'http://localhost:9200/amazon'
curl -XPUT 'localhost:9200/amazon?pretty&pretty'
curl -XPUT 'localhost:9200/amazon/order/0?pretty' -H 'Content-Type: application/json' -d '{}'
curl -XPUT 'localhost:9200/amazon/_mapping/review?pretty'  -H 'Content-Type: application/json' -d'{
"properties": {
"product_name": {
"type":     "text",
"fielddata": true
}
}
}'