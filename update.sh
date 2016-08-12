export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
docker-compose stop
docker-compose rm -f
./gradlew docker
docker-compose up -d