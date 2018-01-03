package com.example.targetdataapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException 
{

	public InvalidRequestException() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InvalidRequestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	public InvalidRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public InvalidRequestException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
