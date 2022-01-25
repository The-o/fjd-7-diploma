.PHONY: build-backend
build-backend:
	docker-compose build diplomaBack

.PHONY: build-frontend
build-frontend:
	docker-compose build diplomaFront

.PHONY: run-backend
run-backend: build-backend
	mkdir -p storage
	docker-compose up diplomaBack

.PHONY: run
run: build-backend build-frontend
	mkdir -p storage
	docker-compose up

.PHONY: test
test:
	$(PWD)/gradlew --no-daemon --console=plain clean test