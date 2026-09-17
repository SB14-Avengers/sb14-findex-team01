package com.sprint.findex.global.exception;

import com.sprint.findex.global.exception.errorcode.BaseErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서버 오류(500) 응답 details 에 내부 예외 메시지를 담을지 여부. 로컬(application-local.yml)에서만 true, 배포에서는 false 라
     * 내부 정보가 클라이언트로 새지 않는다. (로그에는 프로파일과 무관하게 항상 전체 스택트레이스가 남는다)
     */
    private final boolean exposeErrorDetails;

    public GlobalExceptionHandler(
            @Value("${findex.error.expose-details:false}") boolean exposeErrorDetails) {
        this.exposeErrorDetails = exposeErrorDetails;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        log.warn(
                "[BusinessException] status={}, code={}, message={}",
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                e.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        log.warn(
                "[MethodArgumentNotValidException] fieldErrors={}",
                e.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getBindingResult()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.warn(
                "[ConstraintViolationException] violations={}",
                e.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getConstraintViolations()));
    }

    /** 경로변수/쿼리파라미터 타입 불일치 (예: Long 자리에 문자열) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        String requiredType =
                e.getRequiredType() == null ? "알 수 없음" : e.getRequiredType().getSimpleName();
        String details =
                String.format(
                        "%s: '%s' 값을 %s 타입으로 변환할 수 없습니다.", e.getName(), e.getValue(), requiredType);
        log.warn("[MethodArgumentTypeMismatchException] {}", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다.", details));
    }

    /** 요청 본문 파싱 실패 (깨진 JSON, 허용되지 않는 enum 값 등) */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {
        String details = e.getMostSpecificCause().getMessage();
        log.warn("[HttpMessageNotReadableException] {}", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다.", details));
    }

    /** 매핑된 핸들러도 정적 리소스도 없는 경로 요청 → 404 (제네릭 500으로 떨어지지 않도록) */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("[NoResourceFoundException] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.of(
                                HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.", e.getResourcePath()));
    }

    /**
     * DB 제약 위반 (유니크 조합 중복, FK 위반, NOT NULL 위반 등). 보통은 서비스에서 {@code existsBy...} 로 미리 걸러 409({@link
     * BusinessException})로 응답하지만, 동시 요청 레이스로 그 사전 체크를 통과해버린 경우의 마지막 방어선.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e) {
        log.warn("[DataIntegrityViolationException] {}", e.getMostSpecificCause().getMessage());
        String details = exposeErrorDetails ? e.getMostSpecificCause().getMessage() : null;
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT, "요청이 데이터 제약 조건과 충돌합니다.", details));
    }

    /**
     * 낙관적 락 충돌 (예: 이미 다른 요청이 먼저 삭제/수정한 엔티티에 뒤늦게 delete/update 시도). {@code @Version} 필드가 없어도,
     * Hibernate는 엔티티 단위 delete/update가 항상 영향받은 row 1건을 반환하길 기대하는데, 그 사이 다른 트랜잭션이 먼저 지워버리면 0건이 되어 이
     * 예외가 던져진다. 결과적으로 "이미 없는 리소스"이므로 404로 응답한다.
     *
     * <p><b>주의(향후 재검토 필요)</b>: 지금은 어떤 엔티티도 {@code @Version} 필드를 쓰지 않아서, 이 예외는 항상 "동시 삭제 레이스로 row가
     * 이미 사라진" 경우에만 발생한다(row가 실제로 존재하는데 버전만 어긋나는 진짜 낙관적 락 충돌은 현재 발생 불가능). 나중에 어떤 엔티티가
     * {@code @Version}을 도입하면, row는 존재하지만 버전만 어긋난 "진짜 충돌" 케이스도 같은 예외로 여기 들어오게 되므로, 그때는 이 핸들러가
     * 404("이미 삭제됨")로 잘못 응답하지 않도록 원인을 구분하는 로직으로 재검토해야 한다.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException e) {
        log.warn("[ObjectOptimisticLockingFailureException] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND, "요청한 리소스가 이미 삭제되었습니다.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[UnhandledException]", e);
        String details = exposeErrorDetails ? e.getMessage() : null;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.of(
                                HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", details));
    }
}
