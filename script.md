# Scripts

Here, I memo scripts that I have used during development.

## Postgres

### Run Postgres Container

```
// 02.도커로 postgres 컨테이너 실행
docker run --name rest -p 5432:5432 -e 치=pass -d 름
// rest 컨테이너의 이름
// 컨테이너 안에서 5432 포트(뜨는 postgres) 로컬호스트 머신의 5432 포트로 매핑
// -e 환경변수 
// -d 데몬 모드로 띄운다
// postgres 이미지의 이름 postgres로 컨테이너 만들겠다.
```

This cmdlet will create Postgres instance so that you can connect to a database with:
* database: postgres
* username: postgres
* password: pass
* post: 5432

### Getting into the Postgres container

```
// 03. 도커 컨테이너에 들어가보기
docker exec -i -t rest bash
// -i 인터렉티브 모드로
// -t 타겟 컨테이너 지정
// 컨테이너 안에 어떤 명령어 실행할지 (bash)
```

Then you will see the containers bash as a root user.

### Connect to a database

```
psql -d postgres -U postgres
```

### Query Databases

```
\l
```

### Query Tables

```
\dt
```

### Quit

```
\q
```

## application.properties

### Datasource

```
// 04. 데이터소스 설
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Hibernate

```정
// 05. 하이버네이트 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Test Database

```
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
```