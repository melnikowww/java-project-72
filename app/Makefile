clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

run-dist:
	./build/install/app/bin/app

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

update-deps:
	./gradlew useLatestVersions

helpMe:
	./build/install/app/bin/app -h

files:
	./build/install/app/bin/app src/test/resources/file11.yml src/test/resources/file22.yml

.PHONY: build

