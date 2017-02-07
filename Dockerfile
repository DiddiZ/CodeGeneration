FROM openjdk:jdk
COPY *.jar /usr/src/CodeGeneration/
COPY ./src /usr/src/CodeGeneration
COPY ./web /usr/src/CodeGeneration/web
WORKDIR /usr/src/CodeGeneration
RUN javac -sourcepath . -cp guava-21.0.jar:utils-1.4.jar:gson-2.8.0.jar:jopt-simple-6.0-alpha-1.jar  de/diddiz/codegeneration/CodeGeneration.java
VOLUME /usr/src/CodeGeneration/populations
EXPOSE 80
CMD ["java", "-cp", "guava-21.0.jar:utils-1.4.jar:gson-2.8.0.jar:jopt-simple-6.0-alpha-1.jar:.", "de/diddiz/codegeneration/CodeGeneration", "-server", "-poolsize=1000"]