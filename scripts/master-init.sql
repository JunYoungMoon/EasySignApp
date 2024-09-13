-- EasySignApp 스키마 생성
-- 해당 명령은 'EasySignApp'이라는 스키마(데이터베이스)를 생성합니다.
-- 스키마가 이미 존재하는 경우 새로 생성되지 않습니다.
CREATE SCHEMA IF NOT EXISTS `EasySignApp` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 복제 유저 생성
-- 복제(Replication) 전용 사용자 'replication_user'를 생성합니다.
-- 이 사용자 계정은 모든 호스트('%')에서 접근 가능하며, 비밀번호는 'replication_password'입니다.
CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password';

-- 권한 추가
-- 'replication_user' 사용자에게 복제(Slave) 권한을 부여합니다.
-- 이는 모든 데이터베이스와 테이블('*.*')에 대해 복제 권한을 설정합니다.
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';

-- 권한 변경사항 적용
-- 권한 테이블에 대한 변경 사항을 즉시 적용합니다.
FLUSH PRIVILEGES;

-- 읽기 잠금 설정
-- Master에서 테이블이 변경되지 않도록 읽기 잠금을 설정합니다.
-- 이 잠금은 복제 설정 동안 데이터 일관성을 유지하기 위해 사용됩니다.
FLUSH TABLES WITH READ LOCK;

-- Master 상태 확인
-- 현재의 Master 상태를 표시하여 복제에 필요한 이진 로그 파일과 위치를 확인합니다.
SHOW MASTER STATUS;