FROM adoptopenjdk/openjdk11:latest
COPY target/yuna-standalone.jar /
CMD  ["java", "-Dfile.encoding=UTF-8","-jar","yuna-standalone.jar", "-c" ,"/etc/service/config/services", "-s" ,"/etc/service/secret/sercets", "start"]