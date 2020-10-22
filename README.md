A project to dump all the spotify data into Kafka


Create a local file (for example, at $HOME/.confluent/java.config) with configuration parameters to connect to your Kafka cluster. Starting with one of the templates below, customize the file with connection information to your cluster. Substitute your values for {{ BROKER_ENDPOINT }}, {{CLUSTER_API_KEY }}, and {{ CLUSTER_API_SECRET }}

Template configuration file for Confluent Cloud
# Kafka
bootstrap.servers={{ BROKER_ENDPOINT }}
security.protocol=SASL_SSL
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='{{ CLUSTER_API_KEY }}' password='{{ CLUSTER_API_SECRET }}';
sasl.mechanism=PLAIN
