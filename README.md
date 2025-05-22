# 📦 LuizaLabs Logistics - Desafio Técnico

Este projeto Java foi desenvolvido como parte do desafio técnico da vertical **Logística** da **LuizaLabs**. A aplicação utiliza o framework **Spring Boot** e tem como objetivo processar arquivos de pedidos, armazenar dados em memória para testes e permitir consultas rápidas e confiáveis.

---

## 🚀 Tecnologias Utilizadas

- **Java 17 (OpenJDK 17.0.14 LTS):**  
  Utilizado por ser uma versão LTS estável e com suporte robusto, garantindo performance e compatibilidade.

- **Spring Boot 3.x:**  
  Facilita a criação de aplicações web RESTful com configuração mínima, integração fácil e alta produtividade.

- **Maven:**  
  Gerenciador de dependências e build padrão para projetos Java, garantindo fácil compilação, empacotamento e execução.

- **JUnit 5:**  
  Framework moderno para criação de testes unitários, promovendo qualidade e cobertura de código.

- **Lombok:**  
  Biblioteca que reduz boilerplate no código Java, gerando automaticamente getters, setters, construtores, etc.

---

## 💾 Armazenamento de Dados

Os dados dos pedidos são armazenados **em memória** durante a execução da aplicação. Essa abordagem foi adotada para:  

- Facilitar os testes automatizados, garantindo rapidez e isolamento.  
- Evitar dependências externas, como banco de dados, para simplificar a execução local e durante o desenvolvimento.  

Para uso em produção, o armazenamento em banco pode ser implementado conforme necessidade.

---

## ⚙️ Como Rodar o Projeto Localmente

Para rodar o projeto em sua máquina local, siga os passos abaixo:

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/lfverri/luizalabs-logistics.git
    ```

2.  **Acesse o diretório do projeto:**

    ```bash
    cd luizalabs-logistics
    ```

3.  **Compile e execute a aplicação:**

    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Acesse a documentação Swagger para testar a API:**

    ```
    http://localhost:8080/swagger-ui/index.html#/
    ```

5.  **Execute os testes automatizados:**

    ```bash
    ./mvnw test
    ```

---

## 🐳 Rodando com Docker

Se preferir utilizar Docker, siga estas instruções:

1.  **Para construir a imagem Docker:**

    ```bash
    docker build -t luizalabs-logistics .
    ```

2.  **Para rodar o container:**

    ```bash
    docker run -p 8080:8080 --rm --name logistics-app luizalabs-logistics
    ```

3.  **Teste a API pelo navegador ou Swagger em:**

    ```
    http://localhost:8080/swagger-ui/index.html#/
    ```

   
   
   
