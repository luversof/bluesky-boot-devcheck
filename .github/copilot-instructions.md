# Bluesky Boot Autoconfigure Devcheck - GitHub Copilot Instructions

## 프로젝트 개요

Spring Boot 기반 프로젝트에서 개발 내용 확인을 위한 도구를 제공하는 라이브러리입니다.

개발자가 작성한 Controller 메서드와 Utility 정적 메서드 목록을 웹 페이지로 확인할 수 있습니다.

## 주요 기능

- 개발 확인용 Controller 메서드 목록 제공 (`/_check`)
- Utility 정적 메서드 목록 제공 (`/_check/util`)
- Thymeleaf UI 또는 JSON 형식 지원
- 기본 활성화, Properties로 비활성화 가능

## 기술 스택

- Java 17
- Spring Boot 3.4.0+
- Thymeleaf (선택)
- Maven

## 사용 방법

### 1. Maven Dependency

```xml
<dependency>
    <groupId>io.github.luversof</groupId>
    <artifactId>bluesky-boot-autoconfigure-devcheck</artifactId>
    <version>${currentVersion}</version>
</dependency>
```

### 2. 접속

- Controller 목록: `http://localhost:{port}/_check`
- Utility 목록: `http://localhost:{port}/_check/util`

### 3. 비활성화 (Production 환경)

```properties
bluesky-boot.dev-check.enabled=false
```

## 아키텍처

### DevCheckController

- `/_check`: Controller 메서드 목록 반환
- `/_check/util`: Utility 정적 메서드 목록 반환
- Thymeleaf가 있으면 HTML, 없으면 JSON 응답

### DevCheckAutoConfiguration

- Dependency 추가 시 자동으로 설정됨
- `@ConditionalOnProperty`로 활성화/비활성화 제어

### 메서드 수집

- Spring의 `RequestMappingHandlerMapping`을 사용하여 Controller 메서드 수집
- Reflection을 사용하여 Utility 정적 메서드 수집

## 사용 프로젝트

- **bluesky-project**: 개발 중인 API 엔드포인트 확인용
- **bluesky-cloud**: Cloud 서비스의 Health Check 및 개발 확인용

## UI 형식

### Thymeleaf UI (권장)

```html
- Controller: com.example.UserController
  - GET /users: getAllUsers()
  - POST /users: createUser()
  - GET /users/{id}: getUser()
```

### JSON 형식

```json
{
  "controllers": [
    {
      "className": "com.example.UserController",
      "methods": [
        {
          "name": "getAllUsers",
          "mapping": "GET /users"
        }
      ]
    }
  ]
}
```

## 코딩 규칙

### 패키지 구조

```
io.github.luversof.boot.devcheck/
├── controller/                  # DevCheckController
├── config/                      # AutoConfiguration
└── collector/                   # 메서드 수집 로직
```

### 보안 원칙

- **Production 환경에서는 반드시 비활성화**
- 민감한 메서드는 필터링 필요
- IP 기반 접근 제한 고려

### 성능 고려사항

- 메서드 수집은 애플리케이션 시작 시 1회만 수행
- 캐싱을 통해 반복 요청 시 빠른 응답

## Profile 활용

```properties
# 개발 환경에서만 활성화
spring.config.activate.on-profile=dev
bluesky-boot.dev-check.enabled=true

# Production 환경에서 비활성화
spring.config.activate.on-profile=prod
bluesky-boot.dev-check.enabled=false
```
