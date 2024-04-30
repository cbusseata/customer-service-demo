# https://dev.to/pazyp/oracle-19c-with-docker-36m5
# SID default is ORCLCDB
# username: system
build-and-start-oracle-db:
	docker run --name "oracle19.3" -p 1521:1521 \
		-p 5500:5500 \
		-e ORACLE_PDB=orapdb1 \
		-e ORACLE_PWD=topsecretpass \
		-e ORACLE_MEM=3000 \
		-v /opt/oracle/oradata \
		-d oracle/database:19.3.0-ee

start-oracle-db:
	docker start oracle19.3

clean:
	docker-compose rm -svf

kafka-docker:
	docker-compose up zookeeper kafka-docker

kafka-local:
	docker-compose up zookeeper kafka-local
