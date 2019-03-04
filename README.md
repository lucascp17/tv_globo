## Projeto Campanha Rede Globo no Twitter
Escrito por Lucas Carreiro Pinheiro

### Dependências

- Oracle Java v8u201
- Apache Tomcat v8.5.38

### Instalação

- Instale o [Java v8u201](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) no seu sistema (de preferência o JDK).
- É necessário especificar a variável de ambiente JAVA_HOME, apontando para o local onde o Java foi instalado. 
- Gere o arquivo WAR desta aplicação. Digite:  

```bash
$ jar -cvf tv_globo.war *
```
- Baixe o [Apache Tomcat v8.5.38](https://tomcat.apache.org/download-80.cgi).
- Mova o arquivo .war gerado para a pasta "webapps" do Apache Tomcat.
- Num terminal, vá até a pasta raiz do Apache Tomcat. Altere as permissões, caso seja necessário.
- Execute o seguinte comando:

```bash
$ ./bin/startup.sh
```
- Fim!