
### 심플 로그인 프로젝트
<!-- TOC -->
    * [xss 공격방지 추가완료]
    * [csrf 공격방지 추가완료]
    * [jwt 방식 추가완료]
    * [이메일 인증 추가완료]
    * [Rate Limit 추가완료]
    * [Replication DB 추가중]
<!-- TOC -->

![img.png](img.png)


# master db (master-my.cnf)
[mysqld] <br>
server-id=1 <br>
log-bin=mysql-bin <br>
gtid-mode=ON <br>
enforce-gtid-consistency=true

# slave db (slave-my.cnf)
[mysqld] <br>
server-id=2 <br>
log-bin=mysql-bin <br>
gtid-mode=ON <br>
enforce-gtid-consistency=true

## Master에서 실행
CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password'; <br>
GRANT REPLICATION SLAVE ON \*.* TO 'replication_user'@'%'; <br>
#특정 스키마만 적용하고 싶다면 아래의 내용으로 추가 <br>
#GRANT SELECT ON `specific_schema`.* TO 'replication_user'@'%'; <br>
FLUSH PRIVILEGES; <br>
FLUSH TABLES WITH READ LOCK; <br>

SHOW MASTER STATUS; <br>
+------------------+----------+--------------+------------------+-------------------+ <br>
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set | <br>
+------------------+----------+--------------+------------------+-------------------+ <br>
| mysql-bin.000001 | 123      | test         |                  |                   | <br>
+------------------+----------+--------------+------------------+-------------------+ <br>
mysql-bin.000001 < Master에서의 파일 <br>
123 < Master에서의 위치 <br>

## Slave에서 실행
CHANGE MASTER TO <br>
MASTER_HOST='mysql-master', <br>
MASTER_PORT=3306, <br>
MASTER_USER='replication_user', <br>
MASTER_PASSWORD='replication_password', <br>
MASTER_AUTO_POSITION=1;

START SLAVE;

SHOW SLAVE STATUS;
