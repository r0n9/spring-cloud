package vip.fanrong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vip.fanrong.exception.NotFoundException;

@ControllerAdvice
public class ExceptionController {
    private Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    //404页面
    @ExceptionHandler(NotFoundException.class)
    public String notFoundException() throws Exception {
        return "404";
    }
    /*
    //方便调试
    @ExceptionHandler(value = Exception.class)
	public ModelAndView defaultEorrerHandler(HttpServletRequest req, Exception e) throws Exception{
        logger.error("Request: " + req.getRequestURL() + " raised " + e);

		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null){     
			throw e;
		}

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("Global");
        return mav;
	}*/
}
