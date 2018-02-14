package nl.servicehouse.billingengine.api.pub.registration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.CaseFormat;

import nl.servicehouse.billingengine.service.ValidationException;

@ControllerAdvice("nl.servicehouse.billingengine.api.pub.registration")
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler({JsonMappingException.class})
    public ResponseEntity<List<ErrorMessage>> handleMappingException(JsonMappingException e) {
        final String source = e.getPath().stream().map(pe->CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pe.getFieldName())).collect(Collectors.joining("."));
        final String message = source + ": "  + e.getOriginalMessage();
        return new ResponseEntity(Collections.singletonList(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<List<ErrorMessage>> handleProcessingException(JsonProcessingException e) {
        if (e.getCause() != null && e.getCause() instanceof JsonMappingException) {
            return handleMappingException((JsonMappingException)e.getCause());
        }
        final String message = String.format("Invalid JSON, see lineNo: %s, pos: %s ", e.getLocation().getLineNr(), e.getLocation().getColumnNr());
        return new ResponseEntity(Collections.singletonList(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ErrorMessage>> handleMethodInvalidException(MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final List<ErrorMessage> messages = bindingResult.hasErrors() ?
                bindingResult.getAllErrors().stream().map(ExceptionControllerAdvice::getMessageFromBindingError).collect(Collectors.toList()) :
                Collections.emptyList();
        if (messages.isEmpty()) {
            messages.add(new ErrorMessage(e.getMessage()));
        }
        return new ResponseEntity(messages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<List<ErrorMessage>> handleValidationMessage(final ValidationException valException) {
        final List<ErrorMessage> messages = valException.getMessages().stream()
                .map(ErrorMessage::new)
                .collect(Collectors.toList());

        return new ResponseEntity(messages, HttpStatus.BAD_REQUEST);
    }

    public static final ErrorMessage getMessageFromBindingError(final ObjectError error) {
        final String objectName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getObjectName());
        if (error instanceof FieldError) {
            FieldError fe = (FieldError) error;

            final String snakeCaseField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fe.getField());
            return new ErrorMessage(String.format("%s.%s (%s): %s", objectName, snakeCaseField,  fe.getRejectedValue(), fe.getDefaultMessage()));
        } else {
            return new ErrorMessage(objectName+": " + error.getDefaultMessage());
        }
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<List<ErrorMessage>> handleAccessDeniedException(final AccessDeniedException ex) {
        return new ResponseEntity(Collections.singletonList(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<List<ErrorMessage>> handleGeneralException(final Exception e) {
        if (e.getCause() !=null && e.getCause() instanceof  JsonMappingException) {
            return handleMappingException((JsonMappingException)e.getCause());
        } else
        if (e.getCause() != null && e.getCause() instanceof JsonProcessingException) {
            return handleProcessingException((JsonProcessingException)e.getCause());
        }
        LOGGER.error("Uncategorized exception: {}" , e.getMessage(), e);
        return new ResponseEntity(Collections.singletonList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
