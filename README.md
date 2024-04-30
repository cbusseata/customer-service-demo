# Customer Service Demo - Demo Spring Boot Application

Just playing around with various tools:

* Oracle DB
* Spring Boot
* Redis
* Kafka
* Swagger
* Minikube

## Starting the Application

### Prerequisites

Docker, docker-compose.

Oracle DB: follow [these instructions](https://dev.to/pazyp/oracle-19c-with-docker-36m5) to install
Oracle. In particular, download `LINUX.X64_193000_db_home.zip`, and you can unzip it and move the 
`docker-images` to the root of this repo. Then:

```bash
# NOTE: these commands differ slightly from the article
cd docker-images/OracleDatabase/SingleInstance/dockerfiles
./buildContainerImage.sh -v 19.3.0 -e
```

```bash
# Start oracle
# If this is the first time, run:
> make build-and-start-oracle-db
# After that, you can run:
> make oracle-db

# Start Kafka
# Start fresh:
> make clean
# If you are running the app from IDE:
> make kafka-local
# If you are running the app from Docker:
> make kafka-docker

# Start redis
# In another terminal tab/window:
> docker-compose up redis
```

Start the application.

Docker: note that you may need to change dependency hosts in src/main/resources/application.yaml,
this was set up on OS X and thus uses `host.docker.internal`

```bash
# Start the application
# In another terminal tab/window:
> docker-compose up service
```

IDE: just make sure you set the environment variable `SPRING_PROFILES_ACTIVE` to "local"

Seed the Customer DB by issuing a POST request to: http://127.0.0.1:8080/admin/v1/customer/seed

### Endpoints

GraphQL API available at:
http://localhost:8080/graphiql and http://127.0.0.1:8080/graphql

Example query:
```graphql
{
    getCustomer(id:"DD37Cf93aecA6Dc") {
        id
        firstName
        lastName
        company
        city
        country
        phone1
        phone2
        email
        subscriptionDate
        website
    }
}
```

Swagger documentation available at:
http://localhost:8080/swagger-ui/index.html

## Running in Minikube

Install Minikube!

```bash
# Start minikube
minikube start

# Build image using the minikube docker daemon, so the image will be accessible
# NOTE: you will need to make sure you do everything in this same terminal window, or re-run this command
#       because dynatrace needs it too
> eval $(minikube docker-env)
> docker-compose build service-minikube

# If you end up in a bad state and need to restart
minikube delete
minikube start --driver=docker

# Set up dynatrace
kubectl create namespace dynatrace
kubectl apply -f https://github.com/Dynatrace/dynatrace-operator/releases/download/v0.14.2/kubernetes.yaml
kubectl -n dynatrace wait pod --for=condition=ready --selector=app.kubernetes.io/name=dynatrace-operator,app.kubernetes.io/component=webhook --timeout=300s
# Make sure you have the environment file, then source it and apply the k8s manifest
eval export $(cat k8s/dynatrace.env)
envsubst < k8s/dynakube.yaml | kubectl apply -f -

# Create network tunnel to access service locally
minikube tunnel
```

## TODO

* Minikube

## Helpful Commands

### Kubectl

```bash
# Gives some info if container is in ContainerCreating state
kubectl describe pods

#Check the external IP
kubectl get svc
```

