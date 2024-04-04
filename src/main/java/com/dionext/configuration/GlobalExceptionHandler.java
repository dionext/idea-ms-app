package com.dionext.configuration;



import com.dionext.ideaportal.services.IdeaportalPageCreatorService;
import com.dionext.utils.exceptions.BaseExceptionHandler;
import com.dionext.utils.exceptions.ResourceFindException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler
{
    private IdeaportalPageCreatorService ideaportalPageElementService;

    @Autowired
    public void setHikingLandPageElementService(IdeaportalPageCreatorService ideaportalPageElementService) {
        this.ideaportalPageElementService = ideaportalPageElementService;
    }

    @ExceptionHandler(value = {ResourceFindException.class, NoHandlerFoundException.class, NoResourceFoundException.class})
    public final ResponseEntity<Object> handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
        return processException(ex, request, HttpStatus.NOT_FOUND, ideaportalPageElementService);
    }
    @ExceptionHandler()
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) {
        return processException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ideaportalPageElementService);
    }


}
