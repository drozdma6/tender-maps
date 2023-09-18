FROM gradle:jdk17
COPY . /src/procurements_api
WORKDIR /src/procurements_api
RUN gradle bootJar
RUN rm -f build/libs/*-plain.jar

CMD java -jar build/libs/*.jar