## Projeto Campanha Rede Globo no Twitter
Escrito por Lucas Carreiro Pinheiro

### Dependências

- Oracle Java v8u201
- Apache Tomcat v8.5.38

### Instalação

- Instale o [Java v8u201](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) no seu sistema (de preferência o JDK).
- É necessário especificar a variável de ambiente JAVA_HOME, apontando para o local onde o Java foi instalado. 
- Baixe o [Apache Tomcat v8.5.38](https://tomcat.apache.org/download-80.cgi).
- Mova o arquivo ROOT.war, localizado na raiz do projeto, para a pasta "webapps" do Apache Tomcat. Altere as permissões, caso seja necessário.
- Num terminal, vá até a pasta raiz do Apache Tomcat.
- Execute o seguinte comando:

```bash
$ ./bin/startup.sh
```
- Com um browser, abra http://localhost:8080/
- Fim!
