target := backend-service

docker-compose: docker-compose.yml
	docker-compose up
	
build: app.jar
	docker build -t ${target} .

run: docker-compse build 
	docker run -d --rm --env-file .env --name ${target} --network auto-invest_service-network -p 8080:8080 ${target} -d

clean:
	docker stop ${target}
