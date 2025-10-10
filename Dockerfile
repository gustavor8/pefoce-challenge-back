FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de definição do Maven
COPY pom.xml .
COPY .mvn/ .mvn/

# Baixa as dependências
RUN mvn dependency:go-offline

# Copia o resto do código-fonte
COPY src/ ./src/

# Roda o build do Maven para gerar o arquivo .jar
RUN mvn package -DskipTests

# imagem mínima
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o arquivo .jar gerado no estágio de build para a imagem final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8081 do contêiner para o exterior
EXPOSE 8081

# Comando para iniciar a aplicação quando o contêiner rodar
ENTRYPOINT ["java", "-jar", "app.jar"]