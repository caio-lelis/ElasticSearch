FROM maven:3.8.2-jdk-11

WORKDIR /home

COPY . .

RUN mvn clean package -DskipTests

COPY ./entrypoint.sh ./entrypoint.sh

RUN chmod +x ./entrypoint.sh

CMD bash ./entrypoint.sh