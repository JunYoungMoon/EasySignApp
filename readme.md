# 심플 로그인 프로젝트

**EasySignAppVue**의 백엔드 프로젝트입니다. 이 프로젝트를 기반으로 코인 거래소 기능이 추가될 예정입니다.

---

## 목차
1. [구현 내용](#구현-내용)
2. [실행 방법](#실행-방법)
3. [JWT 흐름도](#jwt-흐름도)
4. [CI/CD 흐름도](#cicd-흐름도)

---

## 구현 내용

다음 기능들이 프로젝트에 구현되었습니다:

- **XSS, CSRF 공격 방지**: 보안을 위한 필수 요소로 공격 방지 기능이 추가되었습니다.
- **JWT 추가**: JSON Web Token 기반 인증 구현.
- **CI/CD 추가**: 지속적인 통합 및 배포 자동화.
- **메일 인증 추가**: 사용자 등록 시 메일 인증 기능 구현.
- **Rate Limit 추가**: 요청 속도 제한을 통한 DoS 공격 방지.
- **Replication DB 추가**: 데이터베이스 이중화(복제) 설정.
- **OAuth2 추가**: 소셜 로그인 기능 구현.
- **다중언어 추가**: 글로벌 사용자를 위한 다중 언어 지원.
- **Spring Boot 2 > 3 마이그레이션 완료**: 최신 버전으로 마이그레이션 완료.

---

## 실행 방법

1. **사전 요구 사항**:
    - `Docker`, `Docker-Compose`가 설치되어 있어야 합니다.

2. **실행**:
    - 프로젝트 디렉토리에서 다음 명령어를 실행해 컨테이너를 시작합니다:
    ```bash
    docker-compose up -d
    ```

3. **문제 발생 시**:
    - 설치 중 문제가 발생하면 다음 명령어로 컴포즈 이미지를 모두 삭제하고 다시 시도하세요:
    ```bash
    docker-compose down --volumes --rmi all
    ```

4. **DB 복제 설정**:
    - `docker-compose.yml` 파일과 `config`, `scripts` 디렉토리의 설정 파일로 자동으로 DB replication이 진행되도록 설정되어 있습니다.
    ```
   
5. **swagger 접속**:
   - http://localhost:8080/swagger-ui/index.html 주소를 통해 구현 API를 확인할 수 있습니다.

---

## JWT 흐름도

다음은 **JWT** 인증 흐름을 시각화한 다이어그램입니다:

![JWT 흐름도](images/JWT.png)
*(JWT 토큰 발급과 인증 처리 과정)*

---

## CI/CD 흐름도

다음은 **CI/CD** 프로세스를 나타낸 흐름도입니다:

![CI/CD 흐름도](images/CICD.png)
*(자동화된 지속적 통합 및 배포 절차)*

---
