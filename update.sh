gradlew docker
docker rm -f devinterx/fsggs
docker build -t devinterx/fsggs build/docker

docker-compose stop
docker-compose rm -f
docker-compose up -d