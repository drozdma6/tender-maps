FROM gradle:jdk17
COPY . /src/parser
WORKDIR /src/parser
RUN gradle bootJar
RUN rm -f build/libs/*-plain.jar

CMD java -jar build/libs/*.jar