## Sobre

Este repositório contém o desafio técnico feito para o processo seletivo para a empresa [ada.tech](https://ada.tech/). 
Para mais informações sobre o desafio [veja aqui](https://github.com/raphaeladrien/movies-battle/blob/master/api-docs/prova.pdf).

## Desenvolvido com

[![Java 17](https://img.shields.io/badge/Java-17-red.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.0.6-green.svg)](https://spring.io/projects/spring-boot)
[![H2 Database](https://img.shields.io/badge/H2_Database-2.1.214-blue.svg)](https://www.h2database.com/html/main.html)
[![Docker](https://img.shields.io/badge/Docker-20.10.10-blue.svg)](https://www.docker.com/)

## Começando

A API está pronta para ser executado com o Docker, basta digitar `docker-compose up game` em seu terminal. 
Ela estará disponível em http://localhost:8080/movies-battle. 
Além disto, a documentação da API também estará disponível no endereço http://localhost:3333.

A base de dados será carregada com 3 users mencionados abaixo e com 52 filmes do IMDB durante o processo de inicialização.
* Novos users podem ser adicionados através do endpoint `movies-battle/id/register`
* A quantidade de 52 filmes foi definida uma vez que a quantidade de combinações possíveis seria bastante elevada. (`52!/2!(52-2)!=1.226573e+132`)

### Segurança

API utiliza tokens JWT com o esquema "Bearer" para autenticação dos usuários. Esse método de autenticação é amplamente utilizado na web.

#### Users disponíveis

| Usuário | Senha |
| ------ | ------ |
| ned.stark | 123456
| jon.snow | 123456
| sansa.stark | 123456






