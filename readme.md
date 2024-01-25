# 심플 로그인 프로젝트
<!-- TOC -->
    * [xss 공격방지 추가완료]
    * [csrf 공격방지 추가완료]
    * [jwt 방식 추가완료]
    * [이메일 인증 추가완료]
    * [Rate Limit 추가완료]
    * [Replication DB 추가중]
<!-- TOC -->

## JWT 방식
![img.png](img.png)

## Docker 설정 방법 

### Compose 실행 방법
docker-compose.yml이 존재하는 곳에서 아래의 명령어 입력   
`docker-compose up -d`

### Compose 이미지 모두 제거 방법
`docker-compose down --volumes --rmi all`

## Master,Slave Replication 설정 방법 

### master db (master-my.cnf)
[mysqld]  
server-id=1  
log-bin=mysql-bin  
gtid-mode=ON  
enforce-gtid-consistency=true

### slave db (slave-my.cnf)
[mysqld]  
server-id=2  
log-bin=mysql-bin  
gtid-mode=ON  
enforce-gtid-consistency=true

### Docker DB 접속
`sudo docker exec -it jun-mysql-master-1 mysql -u root -p`     
`sudo docker exec -it jun-mysql-slave-1 mysql -u root -p`   

### Master에서 실행
`CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password';`   
`GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';`

특정 스키마만 적용하고 싶다면 아래의 내용으로 추가   
`GRANT SELECT ON specific_schema.* TO 'replication_user'@'%';`   
`FLUSH PRIVILEGES;`   
`FLUSH TABLES WITH READ LOCK;`

`SHOW MASTER STATUS;`  
File : mysql-bin.000001 (이진 로그 파일. 변경 사항이 로그되는 바이너리 형식의 파일로, 데이터베이스의 변경 내용을 기록)        
Position : 123 (이진 로그 파일에서의 마지막 로그 항목의 위치를 나타낸다.)

### Slave에서 실행
`CHANGE MASTER TO
MASTER_HOST='mysql-master',
MASTER_PORT=3306,
MASTER_USER='replication_user',
MASTER_PASSWORD='replication_password',
MASTER_AUTO_POSITION=1;`

`START SLAVE;`   
`SHOW SLAVE STATUS;`

### 외부에서 접근할 유저 권한 생성
`create user '유저명'@'%' identified by '패스워드';`   
`grant all privileges on 스키마명.* to '유저명'@'%';`   
`FLUSH PRIVILEGES;`

### 동기화 에러시 백업 복구 방법
#### 1.mysqldump 
`sudo docker exec jun-mysql-master-1 mysqldump -h localhost -P 13306 -uroot -p1234 --single-transaction --all-databases --triggers --routines --events > backup.sql`

#### 2.특정 GTID 제거
에러 원인 및 GTID 파악
1. `SELECT * FROM performance_schema.replication_applier_status_by_worker;`
2. `SHOW SLAVE STATUS;`

특정 GTID 제거
1. `STOP SLAVE;`  
2. `SET GLOBAL GTID_PURGED="ddac1846-bb1b-11ee-a439-0242ac120002:9";`
3. `START SLAVE;`

