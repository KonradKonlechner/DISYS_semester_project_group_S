# Distributed Systems Semester Project

## Documentation
- For a more in depth documentation and installation and user guide, please visit our [confluence page](https://webentwicklungsprojekt.atlassian.net/wiki/spaces/DP/overview)
- [RabbitMQ](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)

## Services
- javafx-app
  - contains the GUI
  - communicates with backend service spring_boot_app via REST APIs
- spring_boot_app
  - handles REST Requests from the GUI 
  - sends messages to the appropriate backend services
  - listens for messages that indicate backend task completion
- data_collection_dispatcher
  - receives a customerId
  - sends queues that indicates what backend data collection jobs have been started
  - sends queues that start backend data collection jobs
- station_data_collector
  - receives data on a customer and station and finds all charging data related to this customer and this station
  - sends this data as a queue message
- data_collection_receiver
  - receives data on customer, stations and charges
  - this data is sorted and packaged for further processing
  - sends packaged data
- pdf_generator
  - receives packaged customer charging data
  - retrieves further customer information from database
  - generates a pdf invoice
  - sends job completion message queue

## Database and queues
- Customer Database
	- Contains customer data (id, first name, last name)
	- URL: localhost:30001
- Stations Database
	- Contains station data (id, db_url, latitude, longitude)
	- URL: localhost:30002
- Individual Station Databases
	- Contains customer station data (id, kwh, customer_id)
	- URL Station 1: localhost:30011
	- URL Station 2: localhost:30012
	- URL Station 3: localhost:30013
- Queue
	- URL: localhost:30003
	- Web: localhost:30083

## Requirements
- [Docker](https://docs.docker.com/get-docker/)

## Start
```shell
docker-compose up
```

## RabbitMQ-Dashboard
- [RabbitMQ-Dashboard](http://localhost:30083)
- Username: guest
- Password: guest


## Documentations
