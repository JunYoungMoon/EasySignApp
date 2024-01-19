
### 심플 로그인 프로젝트
<!-- TOC -->
    * [xss 공격방지 추가]
    * [csrf 공격방지 추가]
    * [jwt 방식 추가]
<!-- TOC -->

![img.png](img.png)


# master db (master-my.cnf)
[mysqld]
server-id=1
log-bin=mysql-bin
gtid-mode=ON
enforce-gtid-consistency=true

# slave db (slave-my.cnf)
[mysqld]
server-id=2
log-bin=mysql-bin
gtid-mode=ON
enforce-gtid-consistency=true
