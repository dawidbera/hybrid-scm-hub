#!/bin/bash
awslocal s3 mb s3://scm-order-documents
awslocal sqs create-queue --queue-name scm-order-events
echo "LocalStack resources initialized: S3 bucket and SQS queue created."
