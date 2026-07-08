FROM eclipse-temurin:21-jre-alpine

#Copia el archivo jar del proyecto al contenedor
COPY target/*.jar app.jar

#Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]