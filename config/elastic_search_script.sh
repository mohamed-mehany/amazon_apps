#!/bin/bash
curl -XPUT 'localhost:9200/amazon?pretty&pretty'
curl -XPUT 'localhost:9200/amazon/order/0?pretty' -H 'Content-Type: application/json' -d '{}'


